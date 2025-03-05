package com.example.demo.controllers;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

    @DeleteMapping("/hello")
    public String sayDelete(){
        return "Delete";
    }
}
