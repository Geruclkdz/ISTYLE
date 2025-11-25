package com.istyle.backend.service;

import com.istyle.backend.api.external.UserDTO;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.api.internal.UserInfo;
import com.istyle.backend.mapper.UserMapper;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String username = jwtInterface.extractUsername(jwtToken);

        Optional<User> byUsername = userRepository.findUserByUsername(username);
        if (byUsername.isPresent()) {
            return byUsername.get().getId();
        }

        throw new Exception("User not found for username: " + username);
    }

    @Transactional
    @Override
    public void updateUserProfile(Integer userId, String description, MultipartFile photo) {
        try {
            // Fetch the user and user info
            User user = userRepository.getUserById(userId);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            // Update description if provided
            UserInfo userInfo = user.getUserInfo();
            if (description != null && !description.isEmpty()) {
                userInfo.setDescription(description);
            }

            // Handle photo upload if provided
            if (photo != null && !photo.isEmpty()) {
                // Save under the same uploads directory used for clothes images
                String directoryPath = "./uploads/images";
                Path path = Paths.get(directoryPath);

                // Create directory if it doesn't exist
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                // Define file paths
                String fileExtension = getFileExtension(photo.getOriginalFilename());
                if (fileExtension.isEmpty()) {
                    fileExtension = ".jpg";
                }
                String fileName = userId + "_user_photo" + fileExtension;
                String relativeFilePath = "/images/" + fileName;
                String fullFilePath = directoryPath + "/" + fileName;

                // Save the photo (robust copy, replace if exists)
                Path targetPath = Paths.get(fullFilePath).toAbsolutePath().normalize();
                Files.createDirectories(targetPath.getParent());
                try (var in = photo.getInputStream()) {
                    Files.copy(in, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                // Update the user photo path
                userInfo.setUser_photo(relativeFilePath);
            }

            user.setUserInfo(userInfo);

            // Save the updated user info
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Error saving photo file", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not update user profile", e);
        }
    }


    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }



}
