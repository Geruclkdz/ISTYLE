package com.istyle.backend.service;

import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.external.OutfitDTO;
import com.istyle.backend.api.external.StatusResponseDTO;
import com.istyle.backend.mapper.OutfitMapper;
import com.istyle.backend.repository.OutfitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitImpl implements OutfitInterface{

    private final OutfitRepository outfitRepository;
    private final OutfitMapper outfitMapper;

    @Override
    public Set<OutfitDTO> getUsersOutfits(Integer userId) {
        return outfitRepository.findByUserId(userId)
                .stream()
                .map(outfitMapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public OutfitDTO getOutfitById(Integer id) {
        return outfitMapper.map(outfitRepository.findById(id).orElseThrow());
    }

    @Override
    public ResponseEntity<Object> deleteOutfit(Integer id) {
        outfitRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

    @Override
    public ResponseEntity<Object> addOutfit(OutfitDTO outfitDTO) {
        outfitRepository.save(outfitMapper.map(outfitDTO));
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

}
