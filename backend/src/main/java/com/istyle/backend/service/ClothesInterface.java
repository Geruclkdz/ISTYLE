package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;

import java.util.List;

public interface ClothesInterface {
    void updateClothes(int id, ClothesDTO clothesDTO);
    void deleteClothes(int id);
    List<ClothesDTO> getClothes();
    ClothesDTO getClothesById(int id);
    void addClothes(ClothesDTO clothesDTO);
}
