package com.example.eduspace.common.util;

import lombok.NoArgsConstructor;
import java.security.SecureRandom;

@NoArgsConstructor
public final class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}