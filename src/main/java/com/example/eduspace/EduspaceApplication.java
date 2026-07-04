package com.example.eduspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduspaceApplication {
    public static void main(String[] args) {
        System.out.println("MONGODB_URI = " + System.getenv("MONGODB_URI"));
        SpringApplication.run(EduspaceApplication.class, args);
    }
}
