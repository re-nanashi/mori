package com.mori.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mori.auth.infra.keycloak.KeycloakTokenResponse;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;

    public static TokenResponse from(KeycloakTokenResponse response) {
        return TokenResponse.builder()
                .accessToken(response.getAccessToken())
                .tokenType(response.getTokenType())
                .expiresIn(response.getExpiresIn())
                .build();
    }
}