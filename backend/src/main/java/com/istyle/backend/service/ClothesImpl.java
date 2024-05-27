package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.mapper.ClothesMapper;
import com.istyle.backend.repository.ClothesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothesImpl implements ClothesInterface {
    private final ClothesRepository clothesRepository;
    private final ClothesMapper clothesMapper;

    @Override
    public List<ClothesDTO> getClothes() {
        return clothesRepository.findAll()
                .stream().map(clothesMapper::map)
                .toList();
    }

    @Override
    public ClothesDTO getClothesById(int id) {
        return clothesMapper.map(clothesRepository.findById((long) id).orElseThrow());
    }

    @Override
    public void deleteClothes(int id) {
        clothesRepository.deleteById((long) id);
    }

    @Override
    public void updateClothes(int id, ClothesDTO clothesDTO) {
        clothesRepository.save(clothesMapper.map(clothesDTO));
    }

    @Override
    public void addClothes(ClothesDTO clothesDTO) {
        clothesRepository.save(clothesMapper.map(clothesDTO));
    }
}
