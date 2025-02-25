package com.doveaccelerator.common.util;

import org.apache.commons.lang3.StringUtils;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static boolean isValidEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static String maskSensitiveInfo(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        int length = text.length();
        if (length <= 2) {
            return "*".repeat(length);
        }
        return text.charAt(0) + "*".repeat(length - 2) + text.charAt(length - 1);
    }
    
    public static String truncate(String text, int maxLength) {
        if (StringUtils.isEmpty(text) || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}