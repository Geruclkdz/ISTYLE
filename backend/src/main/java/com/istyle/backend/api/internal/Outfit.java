package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
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
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "outfit_clothes",
            joinColumns = { @JoinColumn(name = "outfit_id")
            },
            inverseJoinColumns = { @JoinColumn(name = "clothes_id")})
            List<Clothes> clothes;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}