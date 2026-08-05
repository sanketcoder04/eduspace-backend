package com.example.eduspace.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "eduspace.google")
public class GoogleOAuthProperties {
    private String clientId;
}