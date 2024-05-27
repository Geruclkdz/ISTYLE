//package com.istyle.backend.controller;
//
//import com.istyle.backend.api.external.OutfitDTO;
//import com.istyle.backend.service.OutfitInterface;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/api/outfits")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
//public class OutfitController {
//
//    private final OutfitInterface outfitInterface;
//
//    @GetMapping
//    public ResponseEntity<List<OutfitDTO>> getOutfits() {
//        return ResponseEntity.ok(outfitInterface.getOutfits());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<OutfitDTO> getOutfitByID(@PathVariable Integer id) {
//        return ResponseEntity.ok(outfitInterface.getOutfitById(id));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> deleteOutfit(@PathVariable Integer id) {
//        return ResponseEntity.ok(outfitInterface.deleteOutfit(id));
//    }
//
//    @PostMapping("/{id}")
//    public ResponseEntity<Object> addOutfit(@RequestBody OutfitDTO outfitDTO) {
//        return ResponseEntity.ok(outfitInterface.addOutfit(outfitDTO));
//    }
//}
