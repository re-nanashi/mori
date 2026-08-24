package com.mori.shared.webmvc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Environment env,
            ServicePublicPaths publicPaths,
            ApiAuthenticationEntryPoint apiAuthEntryPoint,
            ApiAccessDeniedHandler apiAccessDeniedHandler,
            KeycloakJwtAuthenticationConverter jwtAuthConverter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authorizeHttpRequests(auth -> auth
                        .requestMatchers(allPublicPaths(env, publicPaths)).permitAll()
                        .anyRequest().authenticated()

                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(apiAuthEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                )
                .build();
    }

    private String[] allPublicPaths(Environment env, ServicePublicPaths publicPaths) {
        List<String> paths = new ArrayList<>(List.of(publicPaths.paths()));
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            paths.add("/v3/api-docs");
            paths.add("/v3/api-docs/**");
        }

        return paths.toArray(new String[0]);
    }
}