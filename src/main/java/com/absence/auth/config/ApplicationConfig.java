package com.absence.auth.config;

import com.absence.auth.authentication.JwtConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

}
