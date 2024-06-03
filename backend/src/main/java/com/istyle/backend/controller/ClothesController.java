package com.istyle.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.istyle.backend.api.external.*;
import com.istyle.backend.api.internal.Type;
import com.istyle.backend.mapper.CategoryMapper;
import com.istyle.backend.mapper.TypeMapper;
import com.istyle.backend.repository.CategoriesRepository;
import com.istyle.backend.repository.TypesRepository;
import com.istyle.backend.service.JwtInterface;
import com.istyle.backend.service.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.istyle.backend.service.ClothesInterface;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/clothes")
@RequiredArgsConstructor
public class ClothesController {

    private final ClothesInterface clothesInterface;
    private final UserInterface userInterface;
    private final TypesRepository typesRepository;
    private final TypeMapper TypeMapper;
    private final CategoryMapper categoryMapper;
    private final CategoriesRepository categoriesRepository;


    @GetMapping()
    public ResponseEntity<List<ClothesDTO>> getClothes(@RequestHeader("Authorization") String authorizationHeader) {
        try{
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            List<ClothesDTO> clothes = clothesInterface.getUsersClothes(userId);
            return ResponseEntity.ok(clothes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothesDTO> getClothesByID(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer id) {
        try{
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            ClothesDTO clothes = clothesInterface.getUsersClothesById(id, userId);
            return ResponseEntity.ok(clothes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClothes(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateClothes(@PathVariable Integer id, @RequestBody ClothesDTO clothesDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

    @PostMapping
    public ResponseEntity<Object> addClothes(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("type") String typeJson,
            @RequestParam("category") String categoryJson,
            @RequestParam("image") MultipartFile image) {

        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);

            ObjectMapper objectMapper = new ObjectMapper();
            TypeDTO type = objectMapper.readValue(typeJson, TypeDTO.class);
            List<CategoryDTO> categories = objectMapper.readValue(categoryJson, new TypeReference<>() {
            });

            ClothesDTO clothesDTO = new ClothesDTO();
            clothesDTO.setType(type);
            clothesDTO.setCategories(categories);

            clothesInterface.addClothes(clothesDTO, userId, image);

            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/types")
    public ResponseEntity<List<TypeDTO>> getTypes() {
            List<TypeDTO> types = typesRepository.findAll()
                    .stream()
                    .map(TypeMapper::map)
                    .toList();
            return ResponseEntity.ok(types);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoriesRepository.findAll()
                .stream()
                .map(categoryMapper::map)
                .toList();
        return ResponseEntity.ok(categories);
    }

}
