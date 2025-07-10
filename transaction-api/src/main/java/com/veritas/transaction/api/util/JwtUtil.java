package com.veritas.transaction.api.util;

import java.util.Base64;

public class JwtUtil {
    public static String extractUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                int subIndex = payloadJson.indexOf("\"sub\":");
                if (subIndex != -1) {
                    int start = payloadJson.indexOf('"', subIndex + 6) + 1;
                    int end = payloadJson.indexOf('"', start);
                    return payloadJson.substring(start, end);
                }
            }
        } catch (Exception e) {
            // fallback: return null
        }
        return null;
    }
} 