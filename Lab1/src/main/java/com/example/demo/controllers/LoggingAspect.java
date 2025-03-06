package com.example.demo.controllers;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.controllers.HelloController.*(..))")
    public void logBeforeControllerMethods() {
        System.out.println("A request was received in HelloController");
    }
}
