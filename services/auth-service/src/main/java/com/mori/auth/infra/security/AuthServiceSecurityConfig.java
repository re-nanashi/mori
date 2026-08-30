package com.mori.auth.infra.security;

import com.mori.shared.webmvc.security.ServicePublicPaths;
import com.mori.shared.webmvc.security.WebMvcSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(WebMvcSecurityConfig.class)
public class AuthServiceSecurityConfig {
    @Bean
    public ServicePublicPaths authServicePublicPaths() {
        return () -> new String[]{
                "/auth/register",
                "/auth/login",
                "/auth/refresh"
        };
    }
}