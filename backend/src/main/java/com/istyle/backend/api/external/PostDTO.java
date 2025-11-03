package com.istyle.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PostDTO {
    private int id;
    private int userId;
    private OutfitDTO outfitDTO;
    private String text;
    private LocalDateTime createdAt;
    private int starCount;
}
