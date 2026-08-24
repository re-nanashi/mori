package com.mori.shared.webmvc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class WebMvcSecurityBeans {
    private final ObjectMapper objectMapper;

    @Bean
    public ErrorResponseWriter errorResponseWriter() {
        return new ErrorResponseWriter(objectMapper);
    }

    @Bean
    public ApiAuthenticationEntryPoint apiAuthEntryPoint() {
        return new ApiAuthenticationEntryPoint(errorResponseWriter());
    }

    @Bean
    public ApiAccessDeniedHandler apiAccessDeniedHandler() {
        return new ApiAccessDeniedHandler(errorResponseWriter());
    }

    @Bean
    public KeycloakJwtAuthenticationConverter jwtAuthConverter() {
        return new KeycloakJwtAuthenticationConverter();
    }
}