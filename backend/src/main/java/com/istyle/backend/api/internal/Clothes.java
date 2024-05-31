package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.awt.*;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name="clothes")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Clothes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String src;
    private Color color;
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
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "clothes_outfit",
            joinColumns = { @JoinColumn(name = "clothes_id")
            },
            inverseJoinColumns = { @JoinColumn(name = "outfit_id")})
            Set<Outfit> outfits;
    }



