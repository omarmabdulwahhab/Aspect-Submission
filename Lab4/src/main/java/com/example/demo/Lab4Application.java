package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Lab4Application {
	public static void main(String[] args) {
		SpringApplication.run(Lab4Application.class, args);
	}
}
