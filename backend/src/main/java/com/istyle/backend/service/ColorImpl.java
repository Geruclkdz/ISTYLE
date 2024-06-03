package com.istyle.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor


public class ColorImpl implements ColorInterface {

    @Override
    public String determineMainColor(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(bis);

            if (originalImage == null) {
                throw new IOException("Failed to read image");
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Map<Color, Integer> colorCountMap = new HashMap<>();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = originalImage.getRGB(x, y);
                    Color pixelColor = new Color(rgb, true);

                    if (pixelColor.getAlpha() == 0) {
                        continue;
                    }

                    colorCountMap.put(pixelColor, colorCountMap.getOrDefault(pixelColor, 0) + 1);
                }
            }

            Color mainColor = colorCountMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("Could not determine the main color"))
                    .getKey();

            return String.format("#%02x%02x%02x", mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue());
        }
    }
}