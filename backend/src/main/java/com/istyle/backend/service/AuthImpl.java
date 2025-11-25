package com.istyle.backend.service;

import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.mapper.UserMapper;
import com.istyle.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import com.istyle.backend.api.external.AuthResponse;
import com.istyle.backend.api.external.AuthRequest;
import com.istyle.backend.api.external.RegisterRequest;
import com.istyle.backend.api.internal.UserInfo;


@Service
@RequiredArgsConstructor
public class AuthImpl implements AuthInterface {
    private final UserRepository userRepository;
    private final JwtInterface jwtInterface;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists!");
        }
        else if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists!");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setName(request.getName());
        userInfo.setSurname(request.getSurname());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setUserInfo(userInfo);
        user.setUsername(request.getUsername());
        userInfo.setUser(user);
        userRepository.save(user);

        var jwt = jwtInterface.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // Find user by email first (login by email is required)
        var user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Authenticate using username (so that AuthenticationManager/UserDetails use username internally)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        var jwt = jwtInterface.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .build();
    }

    @Override
    public UserDTO verify() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userMapper.map(user);
    }
}
