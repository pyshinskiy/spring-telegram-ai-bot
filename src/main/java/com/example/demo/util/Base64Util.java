package com.example.demo.util;

import java.util.Base64;

public final class Base64Util {

    private Base64Util() {}

    public static String convertImageToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
