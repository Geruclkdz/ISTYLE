package com.istyle.backend.service;

import com.istyle.backend.api.external.OutfitDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OutfitInterface {
    List<OutfitDTO> getUsersOutfits(int userId);
    OutfitDTO getOutfitById(Integer id);
    ResponseEntity<Object> deleteOutfit(Integer id);
    void addOutfit(OutfitDTO outfitDTO, int userId);
    Map<String, Object> createOutfit(Integer userId, String location, List<Integer> categories, boolean useWeatherConditions);
    Map<String, Object> addLayer(Integer userId, String type, String location, List<Integer> categories, boolean useWeatherConditions, List<Integer> selectedIds);
}
