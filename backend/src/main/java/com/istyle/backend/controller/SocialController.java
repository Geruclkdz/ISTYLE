package com.istyle.backend.controller;

import com.istyle.backend.api.external.*;
import com.istyle.backend.api.internal.Follow;
import com.istyle.backend.api.internal.User;
import com.istyle.backend.mapper.UserMapper;
import com.istyle.backend.repository.UserRepository;
import com.istyle.backend.service.FeedInterface;
import com.istyle.backend.service.UserInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
@Slf4j
public class SocialController {

    private final FeedInterface feedInterface;
    private final UserInterface userInterface;
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getFeed(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            Integer currentUserId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            List<PostDTO> feed = (userId == null)
                    ? feedInterface.getUserFeed(currentUserId)
                    : feedInterface.getUserFeed(userId);
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PostMapping("/post")
    public ResponseEntity<Object> shareOutfit(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PostCreateDTO postCreateDTO) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            feedInterface.shareOutfit(postCreateDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new StatusResponseDTO(201));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/post/{postId}/star")
    public ResponseEntity<Object> addStar(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer postId) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            int newCount = feedInterface.addStarToPost(postId, userId);
            return ResponseEntity.ok(Map.of("starCount", newCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CommentDTO commentDTO) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            feedInterface.addCommentToPost(commentDTO, userId);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Integer postId) {
        try {
            List<CommentDTO> comments = feedInterface.getCommentsForPost(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<Object> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            userInterface.updateUserProfile(userId, description, photo);
            return ResponseEntity.ok(new StatusResponseDTO(200));
        } catch (Exception e) {
            log.error("Failed to update profile", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Failed to update profile",
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping(value = "/profile/photo", consumes = "multipart/form-data")
    public ResponseEntity<Object> updateProfilePhoto(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo == null || photo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "Bad Request",
                        "message", "Photo part is missing or empty"
                ));
            }
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            log.info("Received profile photo upload: userId={}, filename={}, size={} bytes",
                    userId,
                    photo.getOriginalFilename(),
                    photo.getSize());
            userInterface.updateUserProfile(userId, null, photo);
            // Return the new photo path so frontend can update immediately
            User user = userRepository.getUserInfoById(userId);
            return ResponseEntity.ok().body(Map.of("photo", user.getUserInfo().getUser_photo()));
        } catch (Exception e) {
            log.error("Failed to update profile photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Failed to update profile photo",
                    "message", e.getMessage()
            ));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            Integer currentUserId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            User user = (userId == null)
                    ? userRepository.getUserInfoById(currentUserId)
                    : userRepository.getUserInfoById(userId);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }


            return ResponseEntity.ok(userMapper.mapInfo(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/profile/search")
    public ResponseEntity<List<UserDTO>> searchByUsername(@RequestParam String username) {
        List<User> users = userRepository.findByUsernameContaining(username);
        return ResponseEntity.ok(users.stream().map(userMapper::mapInfo).collect(Collectors.toList()));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam int followeeId) {
        try{
        Integer followerId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
        feedInterface.followUser(followerId, followeeId);}
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok("User followed successfully");
    }

    @DeleteMapping("/follow")
    public ResponseEntity<?> unfollowUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam int followeeId) {
        try{
        Integer followerId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
        feedInterface.unfollowUser(followerId, followeeId);}
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok("User unfollowed successfully");
    }


    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(@RequestHeader("Authorization") String authorizationHeader                                                      ) {
        try {
            List<User> following = feedInterface.getFollowing(userInterface.getUserIdFromAuthorizationHeader(authorizationHeader))
                    .stream()
                    .map(Follow::getFollowee)
                    .toList();
            return ResponseEntity.ok(
                    following.stream().map(userMapper::mapInfo).collect(Collectors.toList())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
        }
    }


    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Object> deletePost(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer postId) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            feedInterface.deletePost(postId, userId);
            return ResponseEntity.ok(new StatusResponseDTO(200));
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("forbidden")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer postId,
            @PathVariable Integer commentId) {
        try {
            Integer userId = userInterface.getUserIdFromAuthorizationHeader(authorizationHeader);
            feedInterface.deleteComment(commentId, userId);
            return ResponseEntity.ok(new StatusResponseDTO(200));
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("forbidden")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }



}
