package com.istyle.backend.api.external;

import com.istyle.backend.api.internal.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
@NoArgsConstructor

public class UserDTO {
    private int id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String username;
    private String description;
    private Date created_at;
    private String role;
    private String user_photo;
    private Set<UserDTO> following;

}
