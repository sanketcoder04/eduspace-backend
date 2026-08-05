package com.example.eduspace.security.jwt;

import com.example.eduspace.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, JwtTokenType.ACCESS, jwtProperties.getAccessTokenExpiration());
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, JwtTokenType.REFRESH, jwtProperties.getRefreshTokenExpiration());
    }

    @Override
    public String generateToken(UserDetails userDetails, JwtTokenType tokenType, long expiration) {
        return generateToken(Map.of(), userDetails, tokenType, expiration);
    }

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails, JwtTokenType tokenType, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .claim("type", tokenType.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();

    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public JwtTokenType extractTokenType(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return JwtTokenType.valueOf(type);
    }


    @Override
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public String generateClaimsOnlyToken(Map<String, Object> claims, String subject, JwtTokenType tokenType, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .claim("type", tokenType.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractClaimAsString(String token, String claimKey) {
        return extractClaim(token, claims -> claims.get(claimKey, String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}