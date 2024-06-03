package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.awt.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name="clothes")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(exclude = {"outfits"})
public class Clothes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String src;
    private String color;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "type")
    private Type type;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "clothes_category",
            joinColumns = { @JoinColumn(name = "clothes_id")
            },
            inverseJoinColumns = { @JoinColumn(name = "category_id")})
            Set<Category> categories;
    @ManyToMany(mappedBy = "clothes")
    private List<Outfit> outfits;
    }



