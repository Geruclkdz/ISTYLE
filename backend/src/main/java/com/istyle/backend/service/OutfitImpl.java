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

        // Filter clothes by specified categories (IDs). When none selected, include all (including uncategorized).
        boolean filterActive = categories != null && !categories.isEmpty();
        List<Clothes> filteredClothes = allClothes.stream()
                .filter(c -> {
                    if (!filterActive) return true; // no filters selected: include all items
                    // when filters are active: include only clothes that have at least one of the selected categories
                    return c.getCategories() != null && !c.getCategories().isEmpty()
                            && c.getCategories().stream().anyMatch(cat -> categories.contains(cat.getId()));
                })
                .toList();

        if (filteredClothes.isEmpty()) {
            throw new RuntimeException("No clothes match the selected categories.");
        }

        // Weather-based filtering (if enabled)
        Map<String, Object> weatherData = useWeatherConditions ? weatherService.getWeather(location) : Collections.emptyMap();
        boolean isRaining = useWeatherConditions && ((String) weatherData.getOrDefault("condition", "")).toLowerCase().contains("rain");
        boolean isWindy = useWeatherConditions && (double) weatherData.getOrDefault("wind_speed_kph", 0.0) > 20;
        double temperature = useWeatherConditions ? (double) weatherData.getOrDefault("temperature", 25.0) : 25.0;

        // Group clothes by type
        Map<String, List<Clothes>> clothesByType = filteredClothes.stream()
                .collect(Collectors.groupingBy(c -> c.getType().getName()));

        Set<Clothes> selectedOutfit = new HashSet<>();
        List<Float> selectedHues = new ArrayList<>();
        List<String> missingTypes = new ArrayList<>(); // Track missing types

        List<Clothes> tops = clothesByType.getOrDefault("Tops", List.of());
        if (tops.isEmpty()) {
            missingTypes.add("Tops");
        } else {
            Clothes selectedTop = tops.get(new Random().nextInt(tops.size()));
            selectedOutfit.add(selectedTop);
            selectedHues.add(getHueFromHex(selectedTop.getColor()));
        }

        // Add other layers
        addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Bottoms", false, false);
        addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Shoes", isRaining, isWindy);

        if (useWeatherConditions) {
            if (temperature < 10 & (isRaining || isWindy)) {
                addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Mid-Layers", false, false);
                addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Outerwear", isRaining, isWindy);
            } else if (temperature < 20) {
                addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Mid-Layers", isRaining, isWindy);
            }
        } else {
            addClothingLayer(clothesByType, selectedOutfit, selectedHues, missingTypes, "Mid-Layers", false, false);
        }

        // Map the outfit and return with missing types
        Map<String, Object> result = new HashMap<>();
        result.put("outfit", selectedOutfit.stream().map(clothesMapper::map).collect(Collectors.toSet()));
        result.put("missingTypes", missingTypes);
        return result;
    }

    private void addClothingLayer(Map<String, List<Clothes>> clothesByType, Set<Clothes> selectedOutfit,
                                  List<Float> selectedHues, List<String> missingTypes, String type, boolean isRaining, boolean isWindy) {
        List<Clothes> layer = clothesByType.getOrDefault(type, List.of()).stream()
                .filter(c -> isClothingWeatherSuitable(c, isRaining, isWindy))
                .collect(Collectors.toList());
        if (layer.isEmpty()) {
            missingTypes.add(type); // Track missing type
            return;
        }
        Clothes selected = getMatchingClothing(layer, selectedHues);
        selectedOutfit.add(selected);
        selectedHues.add(getHueFromHex(selected.getColor()));
    }




    private boolean matchesSelectedHues(float clothingHue, List<Float> selectedHues) {
        for (Float selectedHue : selectedHues) {
            float complementaryHue = (selectedHue + 180) % 360;
            Float[] analogousHues = new Float[]{
                    (selectedHue + 30) % 360,
                    (selectedHue - 30 + 360) % 360
            };
            if (Math.abs(clothingHue - complementaryHue) <= 30 ||
                    Math.abs(clothingHue - analogousHues[0]) <= 30 ||
                    Math.abs(clothingHue - analogousHues[1]) <= 30) {
                return true;
            }
        }
        return false;
    }

    private boolean isClothingWeatherSuitable(Clothes clothing, boolean isRaining, boolean isWindy) {
        return (!isRaining || clothing.isRainResistant()) &&
                (!isWindy || clothing.isWindResistant());
    }

    private Clothes getMatchingClothing(List<Clothes> clothes, List<Float> selectedHues) {
        return clothes.stream()
                .filter(c -> matchesSelectedHues(getHueFromHex(c.getColor()), selectedHues))
                .findFirst()
                .orElse(clothes.get(new Random().nextInt(clothes.size()))); // Fallback to random if no match
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
}
