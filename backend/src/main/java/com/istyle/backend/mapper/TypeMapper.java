package com.istyle.backend.mapper;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.external.TypeDTO;
import com.istyle.backend.api.internal.Clothes;
import com.istyle.backend.api.internal.Type;
import org.springframework.stereotype.Component;

@Component
public class TypeMapper {
    public TypeDTO map(Type type) {
        return new TypeDTO()
                .setId(type.getId())
                .setName(type.getName());
    }

    public Type map(TypeDTO typeDTO) {
        return new Type()
                .setId(typeDTO.getId())
                .setName(typeDTO.getName());
    }
}
