package com.istyle.backend.mapper;

import com.istyle.backend.api.external.OutfitDTO;
import com.istyle.backend.api.internal.Outfit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutfitMapper {

    public OutfitDTO map(Outfit outfit) {
        return new OutfitDTO()
                .setId(outfit.getId())
                .setClothes(outfit.getClothes());
    }

    public Outfit map(OutfitDTO outfitDTO) {
        return new Outfit()
                .setId(outfitDTO.getId())
                .setClothes(outfitDTO.getClothes());
    }
}
