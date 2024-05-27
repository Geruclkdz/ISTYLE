package com.istyle.backend.service;

import com.istyle.backend.api.external.AuthRequest;
import com.istyle.backend.api.external.AuthResponse;
import com.istyle.backend.api.external.RegisterRequest;
import com.istyle.backend.api.external.UserDTO;

public interface AuthInterface {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    UserDTO verify();
}