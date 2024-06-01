package com.istyle.backend.controller;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.external.OutfitDTO;
import com.istyle.backend.service.OutfitInterface;
import com.istyle.backend.service.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitInterface outfitInterface;
    private final UserInterface userInterface;

    @GetMapping
    public ResponseEntity<Set<OutfitDTO>> getOutfits(@RequestHeader("Authorization") String authorizationHeader) {
        try{
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            Set<OutfitDTO> outfits = outfitInterface.getUsersOutfits(userId);
            return ResponseEntity.ok(outfits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutfitDTO> getOutfitByID(@PathVariable Integer id) {
        return ResponseEntity.ok(outfitInterface.getOutfitById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOutfit(@PathVariable Integer id) {
        return ResponseEntity.ok(outfitInterface.deleteOutfit(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> addOutfit(@RequestBody OutfitDTO outfitDTO) {
        return ResponseEntity.ok(outfitInterface.addOutfit(outfitDTO));
    }
}
