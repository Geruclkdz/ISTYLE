package com.istyle.backend.api.external;

import com.istyle.backend.api.internal.Clothes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OutfitDTO {
    private int id;
    Set<Clothes> clothes;
}