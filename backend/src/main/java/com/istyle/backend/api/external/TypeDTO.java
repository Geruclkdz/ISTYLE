package com.istyle.backend.api.external;

import com.istyle.backend.api.internal.Type;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor

public class TypeDTO {
    private int id;
    private String name;
}
