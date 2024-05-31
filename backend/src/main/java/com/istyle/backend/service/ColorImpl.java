package com.istyle.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor


public class ColorImpl implements ColorInterface {

    @Override
    public Color determineMainColor(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Map<Color, Integer> colorCountMap = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(originalImage.getRGB(x, y));
                colorCountMap.put(pixelColor, colorCountMap.getOrDefault(pixelColor, 0) + 1);
            }
        }

        Color mainColor = null;
        int maxCount = 0;
        for (Map.Entry<Color, Integer> entry : colorCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mainColor = entry.getKey();
            }
        }

        return mainColor;
    }

}