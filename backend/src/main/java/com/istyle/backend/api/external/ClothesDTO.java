package com.istyle.backend.api.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.awt.*;
import java.util.List;


@Data
@Accessors(chain = true)
@NoArgsConstructor

public class ClothesDTO {
    private int id;
    private String src;
    private TypeDTO type;
    private List<CategoryDTO> categories;
    private int userId;
    private String color;
}
