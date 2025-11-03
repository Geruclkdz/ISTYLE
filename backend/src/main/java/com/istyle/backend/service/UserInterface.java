package com.istyle.backend.service;

import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

public interface UserInterface {
    UserDTO getUserById(Integer id);
    UserDTO getUserByEmail(String email);
    UserInfo getUserInfoByEmail(String email);
    Integer getUserIdFromAuthorizationHeader(String authorizationHeader) throws Exception;

    void updateUserProfile(Integer userId, String description, MultipartFile photo);
}
