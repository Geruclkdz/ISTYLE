package com.istyle.backend.mapper;

import com.istyle.backend.repository.FollowRepository;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDTO map(User user) {
        return new UserDTO()
                .setUsername(user.getUsername())
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());
    }

    public User map(UserDTO userDTO) {
        return new User()
                .setUsername(userDTO.getUsername())
                .setId(userDTO.getId())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword());
    }

    public UserDTO mapInfo(User user) {
        return new UserDTO()
                .setUsername(user.getUsername())
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setUser_photo(user.getUserInfo().getUser_photo())
                .setDescription(user.getUserInfo().getDescription());
    }

    public User mapInfo(UserDTO userDTO) {
        return new User()
                .setUsername(userDTO.getUsername())
                .setId(userDTO.getId())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword());
    }
}
