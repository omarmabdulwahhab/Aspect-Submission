package com.example.Lab5.controller;

import com.example.Lab5.model.AuthRequest;
import com.example.Lab5.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class DemoController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        // authenticate user, return JWT
        return ResponseEntity.ok("token or response");
    }

    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public String protectedEndpoint() {
        return "You are authenticated!";
    }

    @GetMapping("/admin")
    @PreAuthorize("isAuthenticated()")
    public String adminOnly() {
        return "Admin content (simulated)";
    }
}