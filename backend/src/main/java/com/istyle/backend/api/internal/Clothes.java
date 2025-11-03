package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name="clothes")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(exclude = {"outfits", "categories", "type", "user"})
@EqualsAndHashCode(exclude = {"outfits", "categories", "type", "user"})
public class Clothes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String src;
    private String color;
    private boolean isRainResistant;
    private boolean isWindResistant;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    @ManyToOne
    @JoinColumn(name = "id_type")
    private Type type;
    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "clothes_category",
            joinColumns = { @JoinColumn(name = "id_clothes")
            },
            inverseJoinColumns = { @JoinColumn(name = "id_category")})
            Set<Category> categories;
    @ManyToMany(mappedBy = "clothes")
    private List<Outfit> outfits;
    }



