package com.istyle.backend.service;

import java.awt.*;
import java.io.IOException;

public interface ColorInterface {
    String determineMainColor(byte[] imageBytes) throws IOException;
}
