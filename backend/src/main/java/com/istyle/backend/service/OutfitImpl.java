//package com.istyle.backend.service;
//
//public class OutfitImpl implements OutfitInterface{
//
//    private final OutfitRepository outfitRepository;
//    private final OutfitMapper outfitMapper;
//
//    @Override
//    public List<OutfitDTO> getOutfits() {
//        return outfitRepository.findAll()
//                .stream().map(outfitMapper::map)
//                .toList();
//    }
//
//    @Override
//    public OutfitDTO getOutfitById(Integer id) {
//        return outfitMapper.map(outfitRepository.findById((long) id).orElseThrow());
//    }
//
//    @Override
//    public ResponseEntity<Object> deleteOutfit(Integer id) {
//        outfitRepository.deleteById((long) id);
//        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
//    }
//
//    @Override
//    public ResponseEntity<Object> addOutfit(OutfitDTO outfitDTO) {
//        outfitRepository.save(outfitMapper.map(outfitDTO));
//        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
//    }
//
//}
