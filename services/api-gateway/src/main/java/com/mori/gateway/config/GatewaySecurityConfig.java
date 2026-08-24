package com.mori.gateway.config;

import com.mori.gateway.exception.GatewayAccessDeniedHandler;
import com.mori.gateway.exception.GatewayAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Environment env;

    private String[] publicPaths() {
        List<String> paths = new ArrayList<>(List.of(
                "/api/v1/auth/**",
                "/actuator/health",
                "/actuator/health/**"
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

    private boolean isDevProfile() {
        return env.acceptsProfiles(Profiles.of("dev"));
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            GatewayAuthenticationEntryPoint authEntryPoint,
            GatewayAccessDeniedHandler accessDeniedHandler,
            KeycloakReactiveJwtAuthenticationConverter reactiveJwtAuthConverter
    ) {
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> {
                    if (isDev) {
                        // TODO: since we have 1 gateway filter chain and 1 each service, how should we make sure that API
                        //  docs only work during "dev" profile
                        // API Docs
                        auth
                                .pathMatchers("/api-docs/**").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()
                                .pathMatchers("/swagger-ui/**").permitAll()
                                .pathMatchers("/swagger-ui.html").permitAll()
                                .pathMatchers("/webjars/**").permitAll();
                    }

                    auth
                            // Actuator
                            .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                            .pathMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")

                            // Auth
                            .pathMatchers("/api/v1/auth/**").permitAll()

                            // Protected
                            .pathMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                            .pathMatchers("/api/v1/**").hasAuthority("ROLE_USER")
                            .anyExchange().authenticated();
                        }
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveJwtAuthConverter))
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }
}