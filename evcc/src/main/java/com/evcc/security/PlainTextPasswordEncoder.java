package com.evcc.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom password encoder để sử dụng plain text password như yêu cầu
 * CHỈ SỬ DỤNG CHO DEVELOPMENT, KHÔNG AN TOÀN CHO PRODUCTION
 */
public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}