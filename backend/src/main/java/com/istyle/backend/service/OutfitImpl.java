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
public class OutfitImpl implements OutfitInterface{

    private final OutfitsRepository outfitsRepository;
    private final OutfitMapper outfitMapper;
    private final ClothesRepository clothesRepository;
    private final ClothesMapper clothesMapper;
    private final UserRepository userRepository;

    @Override
    public List<OutfitDTO> getUsersOutfits(int userId) {
        return outfitsRepository.findByUserId(userId)
                .stream()
                .map(outfitMapper::map)
                .toList();
    }

    @Override
    public Set<ClothesDTO> createOutfit(Integer userId) {
        List<Clothes> allClothes = clothesRepository.findByUserId(userId);
        if (allClothes.isEmpty()) {
            throw new RuntimeException("No clothes available for the given user.");
        }

        Clothes selectedClothes = allClothes.get(new Random().nextInt(allClothes.size()));
        float selectedHue = getHueFromHex(selectedClothes.getColor());

        float complementaryHue = (selectedHue + 180) % 360;
        Float[] analogousHues = new Float[] {
                (selectedHue + 30) % 360,
                (selectedHue - 30 + 360) % 360
        };

        Map<String, List<Clothes>> clothesByType = allClothes.stream()
                .collect(Collectors.groupingBy(c -> c.getType().getName()));

        Set<ClothesDTO> outfit = new HashSet<>();
        outfit.add(clothesMapper.map(selectedClothes));

        Random random = new Random();
        for (String type : clothesByType.keySet()) {
            if (outfit.size() == 3) break;

            List<Clothes> clothesOfType = clothesByType.get(type);
            if (clothesOfType.stream().anyMatch(c -> c.getId() == selectedClothes.getId())) continue;

            Collections.shuffle(clothesOfType, random);

            Optional<Clothes> matchedClothes = clothesOfType.stream()
                    .filter(c -> isMatchingHue(getHueFromHex(c.getColor()), complementaryHue, analogousHues))
                    .findFirst();

            matchedClothes.ifPresent(c -> outfit.add(clothesMapper.map(c)));
        }

        return outfit;
    }

    private float getHueFromHex(String hexColor) {
        Color color = Color.decode(hexColor);
        float[] hsbValues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsbValues[0] * 360;
    }

    private boolean isMatchingHue(float hue, float complementaryHue, Float[] analogousHues) {
        return Math.abs(hue - complementaryHue) < 40 ||
                Math.abs(hue - analogousHues[0]) < 40 ||
                Math.abs(hue - analogousHues[1]) < 40;
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
