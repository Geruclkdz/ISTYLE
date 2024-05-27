package com.istyle.backend.api.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor

public class CategoryDTO {
    private int id;
    private String name;
}
