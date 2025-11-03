package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_outfit", nullable = false)
    private Outfit outfit;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy ="post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

}
