package com.istyle.backend.mapper;

import com.istyle.backend.api.external.CategoryDTO;
import com.istyle.backend.api.external.TypeDTO;
import com.istyle.backend.api.internal.Category;
import com.istyle.backend.api.internal.Type;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CategoryMapper {
    private final UserRepository userRepository;


    public CategoryDTO map(Category category) {
        return new CategoryDTO()
                .setId(category.getId())
                .setUserId(category.getUser().getId())
                .setName(category.getName());
    }

    public Category map(CategoryDTO categoryDTO) {
        return new Category()
                .setId(categoryDTO.getId())
                .setUser(userRepository.getUserById(categoryDTO.getUserId()))
                .setName(categoryDTO.getName());
    }

    public List<CategoryDTO> map(Set<Category> categories) {
        return new ArrayList<>(categories).stream()
                .map(this::map)
                .toList();
    }

    public Set<Category> map(List<CategoryDTO> categoryDTOs) {
        return categoryDTOs.stream()
                .map(this::map)
                .collect(Collectors.toSet());
    }
}
