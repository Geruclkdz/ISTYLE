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
    public Color determineMainColor(byte[] imageBytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bis);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Map<Color, Integer> colorCountMap = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;

                if (alpha == 0) {
                    continue;
                }

                Color pixelColor = new Color(rgb, true);
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