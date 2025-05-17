package com.bostman.controller;

import com.bostman.dto.UserRegisterDTO;
import com.bostman.dto.UserLoginDTO;
import com.bostman.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }
}
