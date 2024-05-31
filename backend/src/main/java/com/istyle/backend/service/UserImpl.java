package com.istyle.backend.service;

import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.api.internal.UserInfo;
import com.istyle.backend.mapper.UserMapper;
import com.istyle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserImpl implements UserInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

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


}
