package com.istyle.backend.service;


import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

public interface ColorInterface {
    Color determineMainColor(MultipartFile image) throws IOException;
}
