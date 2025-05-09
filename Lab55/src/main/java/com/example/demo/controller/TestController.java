package com.example.demo.controller;

import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping
public class TestController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "You are authenticated!";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is public.";
    }
}

@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user, HttpServletResponse response) {
        if (user.get("username").equals("user") && user.get("password").equals("password")) {
            String token = jwtUtil.generateToken("user");

            // BONUS: Return JWT in a cookie
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return Map.of("jwt", token);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
