package com.mori.gateway.config;

import com.mori.gateway.exception.GatewayAccessDeniedHandler;
import com.mori.gateway.exception.GatewayAuthenticationEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            GatewayAuthenticationEntryPoint authenticationEntryPoint,
            GatewayAccessDeniedHandler accessDeniedHandler,
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> {
                    auth
                            // Actuator
                            .pathMatchers("/actuator/health").permitAll()
                            .pathMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")

                            // Auth
                            .pathMatchers("/api/v1/auth/register").permitAll()
                            .pathMatchers("/api/v1/auth/login").permitAll()
                            .pathMatchers("/api/v1/auth/refresh").permitAll()

                            // Protected
                            .pathMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                            .pathMatchers("/api/v1/**").hasAuthority("ROLE_USER")
                            .anyExchange().authenticated();
                        }
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter()))
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}