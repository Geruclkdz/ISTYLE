package com.istyle.backend.service;

import java.awt.*;
import java.io.IOException;

public interface ColorInterface {
    Color determineMainColor(byte[] imageBytes) throws IOException;
}
