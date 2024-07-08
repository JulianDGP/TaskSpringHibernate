package com.example.CRMGym.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final String salt = "salt123"; // Sal personalizada

    @Override
    public String encode(CharSequence rawPassword) {
        return bCryptPasswordEncoder.encode(salt + rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(salt + rawPassword, encodedPassword);
    }
}
