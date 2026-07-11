package com.example.eduspace.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, JwtTokenType tokenType, long expiration);

    String generateToken(Map<String, Object> claims, UserDetails userDetails, JwtTokenType tokenType, long expiration);

    String extractUsername(String token);

    JwtTokenType extractTokenType(String token);

    boolean isTokenExpired(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}