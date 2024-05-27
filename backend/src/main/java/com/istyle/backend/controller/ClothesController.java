package com.istyle.backend.controller;

import com.istyle.backend.api.external.ClothesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.istyle.backend.service.ClothesInterface;
import java.util.List;
import com.istyle.backend.api.external.StatusResponseDTO;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/clothes")
@RequiredArgsConstructor
public class ClothesController {

    private final ClothesInterface clothesInterface;


    @GetMapping()
    public ResponseEntity<List<ClothesDTO>> getClothes() {
        return ResponseEntity.ok(clothesInterface.getClothes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothesDTO> getClothesByID(@PathVariable Integer id) {
        return ResponseEntity.ok(clothesInterface.getClothesById(id));
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
    public ResponseEntity<Object> addClothes(@RequestBody ClothesDTO clothesDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }
}
