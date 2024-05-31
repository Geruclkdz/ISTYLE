package com.istyle.backend.service;

import com.istyle.backend.api.external.CategoryDTO;
import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.internal.Category;
import com.istyle.backend.api.internal.Clothes;
import com.istyle.backend.api.internal.Type;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.mapper.ClothesMapper;
import com.istyle.backend.repository.ClothesRepository;
import com.istyle.backend.repository.TypesRepository;
import com.istyle.backend.repository.UserRepository;
import com.istyle.backend.repository.CategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor


public class ClothesImpl implements ClothesInterface {

    @Value("${application.remove.bg.api.key}")
    private String removeBgApiKey;
    private final ClothesRepository clothesRepository;
    private final ClothesMapper clothesMapper;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final TypesRepository typesRepository;

    @Override
    public List<ClothesDTO> getUsersClothes(int userId) {

        return clothesRepository.findByUserId(userId)
                .stream()
                .map(clothesMapper::map)
                .toList();
    }

    @Override
    public ClothesDTO getUsersClothesById(int id, int userId) {
        return clothesMapper.map(clothesRepository.findById((int) id).orElseThrow());
    }

    @Override
    public void addClothes(ClothesDTO clothesDTO, int userId, MultipartFile image) {
        try {
            Clothes clothes = clothesMapper.map(clothesDTO);

            User user = userRepository.findById(userId).orElseThrow();
            clothes.setUser(user);

            Set<Category> categories = new HashSet<>();
            for (CategoryDTO categoryDTO : clothesDTO.getCategories()) {
                Optional<Category> category = categoriesRepository.findById(categoryDTO.getId());
                category.ifPresent(categories::add);
            }
            clothes.setCategories(categories);

            Type type = typesRepository.findById(clothesDTO.getType().getId()).orElseThrow();
            clothes.setType(type);

            clothes = clothesRepository.save(clothes); // Save the entity and get the updated entity with generated id
            String directoryPath = "images/" + userId;
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String filePath = directoryPath + "/" + clothes.getId() + ".jpg";

            clothes.setSrc(filePath);

            clothesRepository.save(clothes);

            byte[] imageBytes = eraseBackground(image);

            Path imagePath = Paths.get(filePath);
            Files.write(imagePath, imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Could not save clothes", e);
        }
    }

    private byte[] eraseBackground(MultipartFile image) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Api-Key", removeBgApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image_file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("size", "auto");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Resource> response = restTemplate.exchange(
                "https://api.remove.bg/v1.0/removebg",
                HttpMethod.POST,
                requestEntity,
                Resource.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getInputStream().readAllBytes();
        } else {
            throw new RuntimeException("Failed to remove background. Response: " + response);
        }
    }



    @Override
    public void deleteClothes(int id) {
        clothesRepository.deleteById((int) id);
    }

    @Override
    public void updateClothes(int id, ClothesDTO clothesDTO) {
        clothesRepository.save(clothesMapper.map(clothesDTO));
    }

}
