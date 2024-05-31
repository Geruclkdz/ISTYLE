package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClothesInterface {
    void updateClothes(int id, ClothesDTO clothesDTO);
    void deleteClothes(int id);
    List<ClothesDTO> getUsersClothes(int userId);
    ClothesDTO getUsersClothesById(int id, int userId);
    void addClothes(ClothesDTO clothesDTO, int userId, MultipartFile image);
}
