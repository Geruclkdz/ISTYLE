package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "outfit_clothes",
            joinColumns = { @JoinColumn(name = "id_outfit")
            },
            inverseJoinColumns = { @JoinColumn(name = "id_clothes")})
    private List<Clothes> clothes;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "outfit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
