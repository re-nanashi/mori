package com.mori.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {
    private final Environment env;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveApiAuthenticationEntryPoint reactiveAuthEntryPoint,
            ReactiveApiAccessDeniedHandler reactiveAccessDeniedHandler,
            ReactiveKeycloakJwtAuthenticationConverter reactiveJwtAuthConverter
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth
                        .pathMatchers(adminPaths()).hasAuthority("ROLE_ADMIN")
                        .pathMatchers(publicPaths()).permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(reactiveAuthEntryPoint)
                        .accessDeniedHandler(reactiveAccessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveJwtAuthConverter))
                        .authenticationEntryPoint(reactiveAuthEntryPoint)
                        .accessDeniedHandler(reactiveAccessDeniedHandler)
                )
                .build();
    }

    private String[] adminPaths() {
        return new String[]{
                "/api/v1/admin/**",
                "/actuator/**"
        };
    }

    private boolean isDevProfile() {
        return env.acceptsProfiles(Profiles.of("dev"));
    }

    private String[] publicPaths() {
        List<String> paths = new ArrayList<>(List.of(
                "/api/v1/auth/**",
                "/actuator/health",
                "/actuator/health/**",
                "/actuator/info"
        ));

        if (isDevProfile()) {
            paths.add("/api-docs/**");
            paths.add("/v3/api-docs/**");
            paths.add("/swagger-ui.html");
            paths.add("/swagger-ui/**");
            paths.add("/webjars/**");
        }

        return paths.toArray(new String[0]);
    }
}