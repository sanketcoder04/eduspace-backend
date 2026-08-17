package com.example.eduspace.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "eduspace.jwt")
public class JwtProperties {

    private String secretKey;

    private Long accessTokenExpiration;

    private Long refreshTokenExpiration;

    private Long passwordResetTokenExpiration;
}