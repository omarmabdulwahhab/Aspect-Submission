package com.example.Lab5.service;

import com.example.Lab5.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtUtil jwtUtil = new JwtUtil();

    public String authenticate(String username, String password) {
        if ("user".equals(username) && "password".equals(password)) {
            return jwtUtil.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
