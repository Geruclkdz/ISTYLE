//package com.istyle.backend.mapper;
//
//import com.istyle.backend.api.external.OutfitDTO;
//import com.istyle.backend.api.internal.Outfit;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class OutfitMapper {
//
//    public OutfitDTO map(Outfit outfit) {
//        return new OutfitDTO()
//                .setId(outfit.getId())
//                .setTop(outfit.getTop())
//                .setBottom(outfit.getBottom())
//                .setShoes(outfit.getShoes())
//                .setAccessories(outfit.getAccessories());
//    }
//
//    public Outfit map(OutfitDTO outfitDTO) {
//        return new Outfit()
//                .setId(outfitDTO.getId())
//                .setTop(outfitDTO.getTop())
//                .setBottom(outfitDTO.getBottom())
//                .setShoes(outfitDTO.getShoes())
//                .setAccessories(outfitDTO.getAccessories());
//    }
//}
