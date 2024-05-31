package com.istyle.backend.api.internal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.ToString;

@Data
@Entity
@Builder
@Table(name="user_info")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(exclude = "user")
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
