package com.example.demo.controllers;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String processRequest(String methodType) {
        return methodType + " request received";
    }
}
