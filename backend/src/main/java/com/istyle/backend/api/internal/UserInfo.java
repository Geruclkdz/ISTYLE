package com.istyle.backend.api.internal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@Table(name="user_info")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String name;
    private String surname;
    private String description;
}
