package com.istyle.backend.mapper;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.internal.Clothes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClothesMapper {
    private final TypeMapper typeMapper;


    public ClothesDTO map(Clothes clothes) {
        return new ClothesDTO()
                .setId(clothes.getId())
                .setType(typeMapper.map(clothes.getType()))
                .setSrc(clothes.getSrc());
    }

    public Clothes map(ClothesDTO clothesDTO) {
        return new Clothes()
                .setId(clothesDTO.getId())
                .setType(typeMapper.map(clothesDTO.getType()))
                .setSrc(clothesDTO.getSrc());
    }
}
