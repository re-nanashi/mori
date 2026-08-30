package com.mori.auth.infra.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakAdminConfig {
    private final KeycloakProperties keycloakProperties;

    // RestClient for Admin API calls (create user, assign roles, etc.)
    @Bean("keycloakAdminClient")
    public RestClient keycloakAdminClient() {
        return RestClient.builder()
                .baseUrl(keycloakProperties.getBaseUrl()
                        + "/admin/realms/"
                        + keycloakProperties.getRealm())
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // RestClient for token endpoint calls (login, refresh, logout)
    @Bean("keycloakTokenClient")
    public RestClient keycloakTokenClient() {
        return RestClient.builder()
                .baseUrl(keycloakProperties.getBaseUrl()
                        + "/realms/"
                        + keycloakProperties.getRealm()
                        + "/protocol/openid-connect")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}