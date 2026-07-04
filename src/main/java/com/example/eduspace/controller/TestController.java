package com.example.eduspace.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${jwt.secret}")
    private String jwtSecret;


    @GetMapping("/test")
    public String test() {
        return "MongoDB URI: " + mongoUri + "\n" +
                "Mail Username: " + mailUsername + "\n" +
                "Mail Password: " + mailPassword + "\n" +
                "Google Client ID: " + clientId + "\n" +
                "Google Client Secret: " + clientSecret + "\n" +
                "JWT Secret: " + jwtSecret;
    }
}
