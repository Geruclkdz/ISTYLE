package com.istyle.backend.mapper;

import org.springframework.stereotype.Component;
import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;

@Component
public class UserMapper {
    public UserDTO map(User user) {
        return new UserDTO()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setCreated_at(user.getCreated_at());}

    public User map(UserDTO userDTO) {
        return new User()
                .setId(userDTO.getId())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword())
                .setCreated_at(userDTO.getCreated_at());
    }
}
