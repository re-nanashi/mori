package com.mori.auth.infra.keycloak;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminTokenService {
    private final KeycloakProperties keycloakProperties;
    private final RestClient keycloakTokenClient;
    private final StringRedisTemplate redisTemplate;

    private static final String ADMIN_TOKEN_KEY = "keycloak:admin:token";

    public String getAdminToken() {
        // check Redis first
        String cachedToken = redisTemplate.opsForValue()
                .get(ADMIN_TOKEN_KEY);

        if (cachedToken != null ) {
            log.debug("Keycloak admin token retrieved from cache");
            return cachedToken;
        }

        return fetchAndCacheToken();
    }

    @Retry(name = "keycloak", fallbackMethod = "fetchAndCacheTokenFallback")
    private String fetchAndCacheToken() {
        log.info("Fetching new Keycloak admin token");

        KeycloakTokenResponse response = keycloakTokenClient.post()
                .uri("/token")
                .body("grant_type=client_credentials"
                        + "&client_id=" + keycloakProperties.getAdmin().getClientId()
                        + "&client_secret=" + keycloakProperties.getAdmin().getClientSecret())
                .retrieve()
                .body(KeycloakTokenResponse.class);

        if (response == null) {
            log.error("Failed to obtain Keycloak admin token - response was null");
            throw new KeycloakException("Failed to obtain admin token");
        }

        // cache in redis with TTL (30 seconds before actual expiry)
        long ttl = response.getExpiresIn() - 30;
        redisTemplate.opsForValue()
                .set(
                        ADMIN_TOKEN_KEY,
                        response.getAccessToken(),
                        Duration.ofSeconds(ttl)
                );

        log.info("Keycloak admin token cached for {}s", ttl);

        return response.getAccessToken();
    }

    public String fetchAndCacheTokenFallback(Exception ex) {
        log.error("All retries exhausted for Keycloak admin token: {}", ex.getMessage());
        throw new KeycloakException("Authentication service unavailable — please try again later");
    }
}