package com.example.eduspace.security.jwt;

import com.example.eduspace.config.properties.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return null;
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return null;
    }

    @Override
    public String generateToken(UserDetails userDetails, JwtTokenType tokenType, long expiration) {
        return null;
    }

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails, JwtTokenType tokenType, long expiration) {
        return null;
    }

    @Override
    public String extractUsername(String token) {
        return null;
    }

    @Override
    public JwtTokenType extractTokenType(String token) {
        return null;
    }

    @Override
    public boolean isTokenExpired(String token) {
        return false;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    
}