package com.istyle.backend.service;

import com.istyle.backend.api.external.OutfitDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface OutfitInterface {
    Set<OutfitDTO> getUsersOutfits(Integer userId);
    OutfitDTO getOutfitById(Integer id);
    ResponseEntity<Object> deleteOutfit(Integer id);
    ResponseEntity<Object> addOutfit(OutfitDTO outfitDTO);
}