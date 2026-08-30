package com.mori.auth.infra.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String baseUrl;
    private String realm;
    private Admin admin;
    private Web web;

    @Data
    public static class Admin {
        private String clientId;
        private String clientSecret;
    }

    @Data
    public static class Web {
        private String clientId;
        private String clientSecret;
    }
}