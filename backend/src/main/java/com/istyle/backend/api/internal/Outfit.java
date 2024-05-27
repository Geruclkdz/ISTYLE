package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Entity
@Builder
@Table(name="outfit")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToMany(mappedBy = "outfits")
    private Set<Clothes> clothes;
}