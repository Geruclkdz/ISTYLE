package com.istyle.backend.api.internal;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@Table(name = "follows")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_follower", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "id_followee", nullable = false)
    private User followee;

}
