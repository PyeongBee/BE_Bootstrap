package com.bootstrap.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityProcessor {
    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matchPassword(String encodedPassword, String rawPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
