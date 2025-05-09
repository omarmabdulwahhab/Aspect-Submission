package com.example.demo.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Hardcoded user for demo purposes
        if ("user".equals(username)) {
            return new org.springframework.security.core.userdetails.User(
                    "user",
                    "password", // No encoding for simplicity
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
        throw new UsernameNotFoundException("User not found");
    }
}
