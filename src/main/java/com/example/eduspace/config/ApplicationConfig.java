package com.example.eduspace.config;

import com.example.eduspace.config.properties.AppProperties;
import com.example.eduspace.config.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        AppProperties.class
})
public class ApplicationConfig {
}
