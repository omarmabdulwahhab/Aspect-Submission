package com.bostman.service;

import com.bostman.dto.EmailMessage;
import com.bostman.dto.UserRegisterDTO;
import com.bostman.dto.UserLoginDTO;
import com.bostman.entity.Role;
import com.bostman.entity.User;
import com.bostman.repository.UserRepository;
import com.bostman.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;
    private final MessageProducer messageProducer;

    @Override
    public String register(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "Email already registered.";
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .build();

        userRepository.save(user);
//        notificationService.sendEmail(
//                dto.getEmail(),
//                "Welcome to Bostman",
//                "Hi " + dto.getFullName() + ", thanks for registering!"
//        );

        messageProducer.sendEmailMessage(new EmailMessage(
                dto.getEmail(),
                "Welcome to Bostman",
                "Hi " + dto.getFullName() + ", thanks for registering!"
        ));

        return "User registered successfully.";
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

}
