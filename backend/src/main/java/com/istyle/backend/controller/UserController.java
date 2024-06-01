package com.istyle.backend.controller;

import com.istyle.backend.api.external.StatusResponseDTO;
import com.istyle.backend.api.external.UserDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;


@RestController
@RequestMapping("/api/user")

public class UserController {
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable int id) {
        return new UserDTO()
                .setId(id)
                .setEmail("example@example.com");
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponseDTO(200));
    }

}


