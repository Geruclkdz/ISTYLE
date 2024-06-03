package com.istyle.backend.service;

import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.api.internal.UserInfo;
import com.istyle.backend.mapper.UserMapper;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserImpl implements UserInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtInterface jwtInterface;

    @Override
    public UserDTO getUserById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(userMapper::map).orElse(null);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userOptional.map(userMapper::map).orElse(null);
    }
    public UserInfo getUserInfoByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(User::getUserInfo)
                .orElse(null);
    }
    @Override
    public Integer getUserIdFromAuthorizationHeader(String authorizationHeader) throws Exception {
        String jwtToken = authorizationHeader.substring(7);
        String userEmail = jwtInterface.extractEmail(jwtToken);
        UserDTO user = getUserByEmail(userEmail);
        if (user != null) {
            return user.getId();
        } else {
            throw new Exception("User not found for email: " + userEmail);
        }
    }

}
