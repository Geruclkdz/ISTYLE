package com.istyle.backend.service;

import com.istyle.backend.api.external.CategoryDTO;
import com.istyle.backend.api.external.ClothesDTO;
import com.istyle.backend.api.internal.Category;
import com.istyle.backend.api.internal.Clothes;
import com.istyle.backend.api.internal.Type;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.mapper.CategoryMapper;
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

    private final CategoryMapper categoryMapper;
    @Value("${application.remove.bg.api.key}")
    private String removeBgApiKey;
    private final ClothesRepository clothesRepository;
    private final ClothesMapper clothesMapper;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final TypesRepository typesRepository;
    private final ColorInterface colorInterface;

    // Maksymalny rozmiar pliku w bajtach (5 MB)
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    @Override
    public List<ClothesDTO> getUsersClothes(int userId) {
        return clothesRepository.findByUserId(userId)
                .stream()
                .map(clothesMapper::map)
                .toList();
    }

    @Override
    public ClothesDTO getUsersClothesById(int id, int userId) {
        return clothesMapper.map(clothesRepository.findById(id).orElseThrow());
    }

    @Override
    public void addClothes(ClothesDTO clothesDTO, int userId, MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Image file is required");
            }

            // Weryfikacja rozmiaru z metadanych MultipartFile
            if (image.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File too large. Max size is 5 MB");
            }

            // Odczytaj bajty raz, będziemy ich używać wielokrotnie
            byte[] originalBytes = image.getBytes();
            if (originalBytes.length == 0) {
                throw new IllegalArgumentException("Image file is empty");
            }

            // Dodatkowa ochrona - rozmiar po odczycie
            if (originalBytes.length > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File too large. Max size is 5 MB");
            }

            // Weryfikacja content-type
            String contentType = image.getContentType();
            if (contentType == null ||
                    !(contentType.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)
                            || contentType.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE))) {
                throw new IllegalArgumentException("Unsupported file type. Allowed: image/jpeg, image/png");
            }

            // Weryfikacja magic bytes
            if (!isJpeg(originalBytes) && !isPng(originalBytes)) {
                throw new IllegalArgumentException("Invalid image file (magic bytes mismatch). Allowed: JPEG, PNG");
            }

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

            clothes = clothesRepository.save(clothes);

            String directoryPath = "./uploads/images";
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String relativeFilePath = "/images/" + userId + "_" + clothes.getId() + ".jpg";
            String fullFilePath = directoryPath + "/" + userId + "_" + clothes.getId() + ".jpg";

            clothes.setSrc(relativeFilePath);

            // Wywołaj remove.bg na sprawdzonych bajtach
            byte[] imageBytes = eraseBackground(originalBytes, image.getOriginalFilename());
            String color = colorInterface.determineMainColor(imageBytes);
            clothes.setColor(color);
            clothesRepository.save(clothes);

            Path imagePath = Paths.get(fullFilePath);
            Files.write(imagePath, imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Could not read or save image", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not save clothes", e);
        }
    }


    private byte[] eraseBackground(byte[] imageBytes, String originalFilename) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Api-Key", removeBgApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image_file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return originalFilename != null ? originalFilename : "file";
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

    // Sprawdza czy bajty odpowiadają plikowi JPEG (zaczyna się od 0xFF 0xD8)
    private boolean isJpeg(byte[] bytes) {
        if (bytes == null || bytes.length < 2) return false;
        return (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8;
    }

    // Sprawdza czy bajty odpowiadają plikowi PNG (pierwsze 8 bajtów)
    private boolean isPng(byte[] bytes) {
        if (bytes == null || bytes.length < 8) return false;
        int[] pngSignature = new int[]{0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        for (int i = 0; i < pngSignature.length; i++) {
            if ((bytes[i] & 0xFF) != pngSignature[i]) return false;
        }
        return true;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO, int userId) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setUser(userRepository.findById(userId).orElseThrow());
        Category savedCategory = categoriesRepository.save(category);
        return categoryMapper.map(savedCategory);
    }


    @Override
    public void deleteClothes(int id) {
        clothesRepository.deleteById(id);
    }

    @Override
    public void updateClothes(int id, ClothesDTO clothesDTO) {
        clothesRepository.save(clothesMapper.map(clothesDTO));
    }

}
