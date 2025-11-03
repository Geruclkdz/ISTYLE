package com.istyle.backend.controller;

import com.istyle.backend.api.external.*;
import com.istyle.backend.service.OutfitInterface;
import com.istyle.backend.service.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitInterface outfitInterface;
    private final UserInterface userInterface;

    @GetMapping
    public ResponseEntity<List<OutfitDTO>> getOutfits(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            List<OutfitDTO> outfits = outfitInterface.getUsersOutfits(userId);
            return ResponseEntity.ok(outfits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> createOutfit(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("lat") Double latitude, @RequestParam("lon") Double longitude, @RequestParam(value = "categories", required = false) List<Integer> categories, @RequestParam(value = "useWeather", defaultValue = "true") boolean useWeatherConditions) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);

            String locationQuery = latitude + "," + longitude;

            Map<String, Object> clothes = outfitInterface.createOutfit(userId, locationQuery, categories, useWeatherConditions);

            return ResponseEntity.ok(clothes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

    @PostMapping("/save")
    public ResponseEntity<Object> addOutfit(@RequestHeader("Authorization") String authorizationHeader, @RequestBody OutfitDTO outfitDTO) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);

            outfitInterface.addOutfit(outfitDTO, userId);

            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
