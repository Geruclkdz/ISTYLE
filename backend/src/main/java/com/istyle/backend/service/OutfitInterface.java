package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.external.OutfitDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface OutfitInterface {
    List<OutfitDTO> getUsersOutfits(int userId);
    OutfitDTO getOutfitById(Integer id);
    ResponseEntity<Object> deleteOutfit(Integer id);
    void addOutfit(OutfitDTO outfitDTO, int userId);
    Set<ClothesDTO> createOutfit(Integer userId);
}