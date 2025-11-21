package com.istyle.backend.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImagesController {

    private static final String UPLOAD_DIR = "./uploads/images";

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path imagePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();

            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                // Default to JPEG if unknown, most of your files appear to be .jpg
                contentType = MediaType.IMAGE_JPEG_VALUE;
            }

            InputStream is = Files.newInputStream(imagePath);
            InputStreamResource resource = new InputStreamResource(is);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
