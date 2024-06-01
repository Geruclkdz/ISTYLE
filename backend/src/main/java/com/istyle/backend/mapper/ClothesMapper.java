package com.istyle.backend.mapper;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.internal.Clothes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClothesMapper {
    private final TypeMapper typeMapper;
    private final CategoryMapper categoryMapper;

    public ClothesDTO map(Clothes clothes) {
        return new ClothesDTO()
                .setId(clothes.getId())
                .setSrc(clothes.getSrc())
                .setUserId(clothes.getUser().getId())
                .setType(typeMapper.map(clothes.getType()))
                .setCategories(categoryMapper.map(clothes.getCategories()))
                .setColor(clothes.getColor());
    }

    public Clothes map(ClothesDTO clothesDTO) {
        return new Clothes()
                .setId(clothesDTO.getId())
                .setSrc(clothesDTO.getSrc())
                .setType(typeMapper.map(clothesDTO.getType()))
                .setCategories(categoryMapper.map(clothesDTO.getCategories()))
                .setColor(clothesDTO.getColor());
    }
}
