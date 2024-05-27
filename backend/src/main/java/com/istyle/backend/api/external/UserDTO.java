package com.istyle.backend.api.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor

public class UserDTO {
    private int id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String description;
    private Date created_at;
    private String role;
}
