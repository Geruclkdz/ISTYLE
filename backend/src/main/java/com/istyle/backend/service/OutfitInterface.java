package com.istyle.backend.service;

import com.istyle.backend.api.external.OutfitDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OutfitInterface {
    List<OutfitDTO> getOutfits();
    OutfitDTO getOutfitById(Integer id);
    ResponseEntity<Object> deleteOutfit(Integer id);
    ResponseEntity<Object> addOutfit(OutfitDTO outfitDTO);
}