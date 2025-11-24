package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.external.OutfitDTO;
import com.istyle.backend.api.external.StatusResponseDTO;
import com.istyle.backend.api.internal.Clothes;
import com.istyle.backend.api.internal.Outfit;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.mapper.ClothesMapper;
import com.istyle.backend.mapper.OutfitMapper;
import com.istyle.backend.repository.ClothesRepository;
import com.istyle.backend.repository.OutfitsRepository;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitImpl implements OutfitInterface {

    private final OutfitsRepository outfitsRepository;
    private final OutfitMapper outfitMapper;
    private final ClothesRepository clothesRepository;
    private final ClothesMapper clothesMapper;
    private final UserRepository userRepository;
    private final WeatherInterface weatherService;

    // Random używany dla wszystkich losowań (równomierne)
    private final Random random = new Random();

    // Hue bounds (stopnie)
    private static final float MONOCHROMATIC_BOUND = 10f;
    private static final float ANALOGOUS_BOUND = 25f;
    private static final float COMPLEMENTARY_BOUND = 30f;

    @Override
    public List<OutfitDTO> getUsersOutfits(int userId) {
        return outfitsRepository.findByUserId(userId)
                .stream()
                .map(outfitMapper::map)
                .toList();
    }

    @Override
    public Map<String, Object> createOutfit(Integer userId, String location, List<Integer> categories, boolean useWeatherConditions) {
        // Fetch user's clothes
        List<Clothes> allClothes = clothesRepository.findByUserId(userId);
        if (allClothes.isEmpty()) {
            throw new RuntimeException("No clothes available for the given user.");
        }

        // Category filtering (zachowano obecne zachowanie)
        boolean filterActive = categories != null && !categories.isEmpty();
        List<Clothes> filteredClothes = allClothes.stream()
                .filter(c -> {
                    if (!filterActive) return true;
                    return c.getCategories() != null && !c.getCategories().isEmpty()
                            && c.getCategories().stream().anyMatch(cat -> categories.contains(cat.getId()));
                })
                .toList();

        if (filteredClothes.isEmpty()) {
            throw new RuntimeException("No clothes match the selected categories.");
        }

        // Weather data
        Map<String, Object> weatherDataRaw = useWeatherConditions ? weatherService.getWeather(location) : Collections.emptyMap();
        Map<String, Object> weatherField = null;
        boolean isRaining = false;
        boolean isWindy = false;
        double temperature = 25.0;
        if (useWeatherConditions) {
            // Normalize external weather data into required fields (temperature in Celsius, isRaining, isWindy)
            Object tempObj = weatherDataRaw.getOrDefault("temperature", weatherDataRaw.getOrDefault("temp_c", 25.0));
            if (tempObj instanceof Number) temperature = ((Number) tempObj).doubleValue();
            else try { temperature = Double.parseDouble(tempObj.toString()); } catch (Exception ignored) {}

            String condition = String.valueOf(weatherDataRaw.getOrDefault("condition", weatherDataRaw.getOrDefault("weather", ""))).toLowerCase();
            isRaining = condition.contains("rain") || condition.contains("drizzle") || condition.contains("shower");

            Object windObj = weatherDataRaw.getOrDefault("wind_speed_kph", weatherDataRaw.getOrDefault("wind_kph", weatherDataRaw.getOrDefault("wind_mph", 0.0)));
            double windKph = 0.0;
            if (windObj instanceof Number) windKph = ((Number) windObj).doubleValue();
            else try { windKph = Double.parseDouble(windObj.toString()); } catch (Exception ignored) {}
            // Decide windy by threshold (kph)
            isWindy = windKph > 20.0;

            weatherField = new HashMap<>();
            weatherField.put("temperature", temperature);
            weatherField.put("isRaining", isRaining);
            weatherField.put("isWindy", isWindy);
        }

        // Group clothes by type
        Map<String, List<Clothes>> clothesByType = filteredClothes.stream()
                .collect(Collectors.groupingBy(c -> c.getType().getName()));

        Set<Clothes> selectedOutfit = new HashSet<>();
        List<Float> selectedHues = new ArrayList<>();
        List<String> comments = new ArrayList<>(); // previously missingTypes, now comments

        // Determine required layers based on temperature (zależnie od zakresów)
        boolean needMidLayer = false;
        boolean needOuterwear = false;
        if (useWeatherConditions) {
            if (temperature < 10.0) {
                needMidLayer = true;
                needOuterwear = true;
            } else if (temperature < 15.0) {
                needOuterwear = true;
            } else if (temperature < 20.0) {
                needMidLayer = true;
            } // else base only
        } // else when weather disabled, only base layers

        // Selection sequence: Tops, Bottoms, Shoes, Mid-layer, Outerwear
        selectTop(clothesByType, selectedOutfit, selectedHues, comments);
        selectLayerGeneric("Bottoms", clothesByType, selectedOutfit, selectedHues, comments, isRaining, isWindy);
        selectLayerGeneric("Shoes", clothesByType, selectedOutfit, selectedHues, comments, isRaining, isWindy);

        if (useWeatherConditions) {
            if (needMidLayer) selectLayerGeneric("Mid-Layers", clothesByType, selectedOutfit, selectedHues, comments, isRaining, isWindy);
            if (needOuterwear) selectLayerGeneric("Outerwear", clothesByType, selectedOutfit, selectedHues, comments, isRaining, isWindy);
        }

        // Prepare result (backward compatibility: include missingTypes key too)
        Map<String, Object> result = new HashMap<>();
        Set<ClothesDTO> dtoSet = selectedOutfit.stream().map(clothesMapper::map).collect(Collectors.toSet());
        result.put("outfit", dtoSet);
        result.put("comments", comments);
        result.put("missingTypes", comments); // backward compatibility
        result.put("weather", useWeatherConditions ? weatherField : null);
        return result;
    }

    private void selectTop(Map<String, List<Clothes>> clothesByType, Set<Clothes> selectedOutfit, List<Float> selectedHues, List<String> comments) {
        List<Clothes> tops = clothesByType.getOrDefault("Tops", List.of());
        if (tops.isEmpty()) {
            comments.add("No items available for required layer. [Tops]");
            return;
        }
        // First top: no hue constraint, choose random uniformly
        Clothes selectedTop = tops.get(random.nextInt(tops.size()));
        selectedOutfit.add(selectedTop);
        selectedHues.add(getHueFromHex(selectedTop.getColor()));
    }

    private void selectLayerGeneric(String type, Map<String, List<Clothes>> clothesByType, Set<Clothes> selectedOutfit,
                                    List<Float> selectedHues, List<String> comments, boolean isRaining, boolean isWindy) {
        List<Clothes> candidates = clothesByType.getOrDefault(type, List.of());
        if (candidates.isEmpty()) {
            comments.add("No items available for required layer. [" + type + "]");
            return;
        }

        Clothes chosen = chooseClothingForType(candidates, selectedHues, isRaining, isWindy, type, comments);
        if (chosen != null) {
            selectedOutfit.add(chosen);
            selectedHues.add(getHueFromHex(chosen.getColor()));
        }
    }

    // Main chooser implementing fallback order and comments
    private Clothes chooseClothingForType(List<Clothes> candidates, List<Float> selectedHues,
                                          boolean isRaining, boolean isWindy, String type, List<String> comments) {

        boolean weatherActive = isRaining || isWindy;

        // Partition candidates
        List<Clothes> weatherAndColor = candidates.stream()
                .filter(c -> isClothingWeatherSuitable(c, isRaining, isWindy))
                .filter(c -> matchesAnyMode(getHueFromHex(c.getColor()), selectedHues))
                .collect(Collectors.toList());

        List<Clothes> colorMatch = candidates.stream()
                .filter(c -> matchesAnyMode(getHueFromHex(c.getColor()), selectedHues))
                .collect(Collectors.toList());

        List<Clothes> weatherOnly = candidates.stream()
                .filter(c -> isClothingWeatherSuitable(c, isRaining, isWindy))
                .collect(Collectors.toList());

        // If no hues selected yet (only possible for first layer which is Tops handled elsewhere),
        // fall back to weatherOnly or random. But general rule: if selectedHues empty -> choose random (or weatherOnly if weather active).
        if (selectedHues.isEmpty()) {
            if (weatherActive && !weatherOnly.isEmpty()) {
                return uniformPick(weatherOnly);
            }
            return uniformPick(candidates);
        }

        // Fallback order: weather+color -> color -> weather -> random
        if (!weatherAndColor.isEmpty()) {
            return uniformPick(weatherAndColor);
        }
        if (!colorMatch.isEmpty()) {
            // color match exists but not weather-suitable (if weatherActive), if weatherActive we should prefer weather-resistant,
            // but per fallback order color takes precedence over weather-only.
            if (weatherActive) {
                comments.add("No weather-resistant item available. Using non-resistant item. [" + type + "]");
            }
            return uniformPick(colorMatch);
        }
        if (weatherActive && !weatherOnly.isEmpty()) {
            // weather-suitable but color mismatch
            comments.add("No suitable color match found. Using weather-resistant item. [" + type + "]");
            return uniformPick(weatherOnly);
        }

        // final fallback: random
        comments.add("No suitable color match found. Using random item. [" + type + "]");
        return uniformPick(candidates);
    }

    private Clothes uniformPick(List<Clothes> list) {
        if (list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    // Checks if clothing is suitable for current weather (both rain and wind conditions must be satisfied)
    private boolean isClothingWeatherSuitable(Clothes clothing, boolean isRaining, boolean isWindy) {
        if (isRaining && !clothing.isRainResistant()) return false;
        if (isWindy && !clothing.isWindResistant()) return false;
        return true;
    }

    // Determine if clothing hue matches any of selected hues by any mode (monochromatic, analogous, complementary)
    private boolean matchesAnyMode(float clothingHue, List<Float> selectedHues) {
        for (Float selected : selectedHues) {
            float d = hueDistance(clothingHue, selected);
            // Monochromatic
            if (d <= MONOCHROMATIC_BOUND) return true;
            // Analogous
            if (d <= ANALOGOUS_BOUND) return true;
            // Complementary: distance to (selected+180) mod 360
            float complementary = Math.abs(hueDistance(clothingHue, (selected + 180f) % 360f));
            if (complementary <= COMPLEMENTARY_BOUND) return true;
        }
        return false;
    }

    // Minimal circular difference between two hues in degrees [0, 180]
    private float hueDistance(float a, float b) {
        float diff = Math.abs(a - b) % 360f;
        return diff > 180f ? 360f - diff : diff;
    }

    private float getHueFromHex(String hexColor) {
        Color color = Color.decode(hexColor);
        float[] hsbValues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsbValues[0] * 360;
    }

    @Override
    public OutfitDTO getOutfitById(Integer id) {
        return outfitMapper.map(outfitsRepository.findById(id).orElseThrow());
    }

    @Override
    public ResponseEntity<Object> deleteOutfit(Integer id) {
        outfitsRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

    @Override
    public void addOutfit(OutfitDTO outfitDTO, int userId) {
        try {
            Outfit outfit = outfitMapper.map(outfitDTO);
            User user = userRepository.findById(userId).orElseThrow();
            outfit.setUser(user);

            List<Clothes> clothes = new ArrayList<>();
            for (ClothesDTO clothesDTO : outfitDTO.getClothes()) {
                Optional<Clothes> cloth = clothesRepository.findById(clothesDTO.getId());
                cloth.ifPresent(clothes::add);
            }
            outfit.setClothes(clothes);

            outfitsRepository.save(outfit);
        } catch (Exception e) {
            throw new RuntimeException("Could not save clothes", e);
        }
    }

    @Override
    public Map<String, Object> addLayer(Integer userId, String type, String location, List<Integer> categories, boolean useWeatherConditions, List<Integer> selectedIds) {
        // Validate type
        String typeKey = switch (type.toUpperCase()) {
            case "MID_LAYER", "MID-LAYER", "MIDLAYER" -> "Mid-Layers";
            case "OUTERWEAR", "OUTER-WEAR", "OUTER_WEAR", "OUTER" -> "Outerwear";
            default -> type;
        };

        // Fetch user's clothes and filter by categories
        List<Clothes> allClothes = clothesRepository.findByUserId(userId);
        boolean filterActive = categories != null && !categories.isEmpty();
        List<Clothes> filteredClothes = allClothes.stream()
                .filter(c -> {
                    if (!filterActive) return true;
                    return c.getCategories() != null && !c.getCategories().isEmpty()
                            && c.getCategories().stream().anyMatch(cat -> categories.contains(cat.getId()));
                })
                .toList();

        Map<String, Object> weatherField = null;
        boolean isRaining = false;
        boolean isWindy = false;
        double temperature = 25.0;
        if (useWeatherConditions) {
            Map<String, Object> weatherDataRaw = weatherService.getWeather(location);
            Object tempObj = weatherDataRaw.getOrDefault("temperature", weatherDataRaw.getOrDefault("temp_c", 25.0));
            if (tempObj instanceof Number) temperature = ((Number) tempObj).doubleValue();
            else try { temperature = Double.parseDouble(tempObj.toString()); } catch (Exception ignored) {}
            String condition = String.valueOf(weatherDataRaw.getOrDefault("condition", weatherDataRaw.getOrDefault("weather", ""))).toLowerCase();
            isRaining = condition.contains("rain") || condition.contains("drizzle") || condition.contains("shower");
            Object windObj = weatherDataRaw.getOrDefault("wind_speed_kph", weatherDataRaw.getOrDefault("wind_kph", weatherDataRaw.getOrDefault("wind_mph", 0.0)));
            double windKph = 0.0;
            if (windObj instanceof Number) windKph = ((Number) windObj).doubleValue();
            else try { windKph = Double.parseDouble(windObj.toString()); } catch (Exception ignored) {}
            // Decide windy by threshold (kph)
            isWindy = windKph > 20.0;
            weatherField = new HashMap<>();
            weatherField.put("temperature", temperature);
            weatherField.put("isRaining", isRaining);
            weatherField.put("isWindy", isWindy);
        }

        List<Clothes> candidates = filteredClothes.stream().filter(c -> c.getType() != null && typeKey.equals(c.getType().getName())).collect(Collectors.toList());
        List<Float> selectedHues = new ArrayList<>();
        if (selectedIds != null && !selectedIds.isEmpty()) {
            for (Integer id : selectedIds) {
                clothesRepository.findById(id).ifPresent(c -> selectedHues.add(getHueFromHex(c.getColor())));
            }
        }

        List<String> comments = new ArrayList<>();
        if (candidates.isEmpty()) {
            comments.add("No items available for required layer. [" + typeKey + "]");
            Map<String, Object> resp = new HashMap<>();
            resp.put("layer", null);
            resp.put("comments", comments);
            return resp;
        }

        Clothes chosen = chooseClothingForType(candidates, selectedHues, isRaining, isWindy, typeKey, comments);
        Map<String, Object> resp = new HashMap<>();
        resp.put("layer", chosen != null ? clothesMapper.map(chosen) : null);
        resp.put("comments", comments);
        return resp;
    }
}
