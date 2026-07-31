package com.example.eduspace;

import com.example.eduspace.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @GetMapping("/")
    public String greet() {
        return "Welcome to Edu Hub";
    }

    @GetMapping("/test")
    public String test() {
        return "MongoDB URI: " + mongoUri + "\n" +
                "Mail Username: " + mailUsername + "\n" +
                "Mail Password: " + mailPassword + "\n" +
                "Google Client ID: " + clientId + "\n" +
                "Google Client Secret: " + clientSecret + "\n" +
                "JWT Secret: " + jwtSecret;
    }

    @GetMapping("/enable")
    public String getEncodedPassword() {
        return passwordEncoder.encode("Abc@123");
    }

    @GetMapping("/manager")
    public String manager() {
        return authenticationManager.toString();
    }

    @GetMapping("/authenticate")
    public String hello() {
        return "Authenticated";
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        UserDetails user = User
                .withUsername("sanket@gmail.com")
                .password("")
                .roles("STUDENT")
                .build();

        return jwtService.generateAccessToken(user);
    }
}
