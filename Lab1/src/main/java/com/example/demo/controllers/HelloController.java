package com.example.demo.controllers;

import com.example.demo.controllers.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping
    public String sayHello() {
        return helloService.processRequest("GET");
    }

    @PostMapping
    public String sayPost() {
        return helloService.processRequest("POST");
    }

    @PutMapping
    public String sayPut() {
        return helloService.processRequest("PUT");
    }

    @DeleteMapping
    public String sayDelete() {
        return helloService.processRequest("DELETE");
    }
}
