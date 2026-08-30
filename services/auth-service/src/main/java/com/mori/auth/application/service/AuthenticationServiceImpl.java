package com.mori.auth.application.service;

import com.mori.auth.api.dto.*;
import com.mori.auth.application.dto.AuthenticationResult;
import com.mori.auth.application.dto.CookiePayload;
import com.mori.auth.application.exception.EmailAlreadyExistsException;
import com.mori.auth.application.exception.InvalidCredentialsException;
import com.mori.auth.application.exception.InvalidTokenException;
import com.mori.auth.application.exception.UsernameAlreadyExistsException;
import com.mori.auth.domain.model.Email;
import com.mori.auth.domain.model.Username;
import com.mori.auth.infra.keycloak.KeycloakAdminTokenService;
import com.mori.auth.infra.keycloak.KeycloakException;
import com.mori.auth.infra.keycloak.KeycloakProperties;
import com.mori.auth.infra.keycloak.KeycloakTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final KeycloakAdminTokenService adminTokenService;
    private final KeycloakProperties keycloakProperties;

    private final RestClient keycloakAdminClient;
    private final RestClient keycloakTokenClient;

    @Override
    public AuthenticationResult<TokenResponse> register(RegisterRequest request) {
        String adminToken = adminTokenService.getAdminToken();

        // normalize email and username
        Email email = Email.of(request.getEmail());
        Username username = Username.of(request.getUsername());

        // check if email or username already exists - throws if already taken
        checkEmailNotTaken(adminToken, email.getValue());
        checkUsernameNotTaken(adminToken, username.getValue());

        // create user in Keycloak
        keycloakAdminClient.post()
                .uri("/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .body(buildUserRepresentation(request, email.getValue(), username.getValue()))
                .retrieve()
                .toBodilessEntity();

        log.info("User created in Keycloak: {}", request.getEmail());

        // auto-login - return JWT immediately
        return login(new LoginRequest(request.getEmail(), request.getPassword()));
    }

    private void checkEmailNotTaken(String adminToken, String email) {
        List<Map<String, Object>> existing = keycloakAdminClient.get()
                .uri("/users?email=" + email + "&exact=true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (existing != null && !existing.isEmpty()) {
            log.warn("The provided email address '{}' is already associated with an account.", email);
            throw new EmailAlreadyExistsException();
        }
    }

    private void checkUsernameNotTaken(String adminToken, String username) {
        List<Map<String, Object>> existing = keycloakAdminClient.get()
                .uri("/users?username=" + username + "&exact=true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (existing != null && !existing.isEmpty()) {
            log.warn("The provided username '{}' is already associated with an account.", username);
            throw new UsernameAlreadyExistsException();
        }
    }

    private Map<String, Object> buildUserRepresentation(
            RegisterRequest request,
            String email,
            String username
    ) {
        return Map.of(
                "username", username,
                "email", email,
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "enabled", true, // enabled but unverified - value will only be false if disabled by an admin
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );
    }

    private String getUserId(String adminToken, String email) {
        List<Map<String, Object>> users = keycloakAdminClient.get()
                .uri("/users?email=" + email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (users == null || users.isEmpty()) {
            throw new KeycloakException("User not found after creation");
        }

        return (String) users.getFirst().get("id");
    }

    @Override
    public AuthenticationResult<TokenResponse> login(LoginRequest request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getWeb().getClientId());
        body.add("client_secret", keycloakProperties.getWeb().getClientSecret());
        body.add("username", request.getEmail());
        body.add("password", request.getPassword());

        KeycloakTokenResponse token = keycloakTokenClient.post()
                .uri("/token")
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.warn("Login failed for email: {}", request.getEmail());
                    throw new InvalidCredentialsException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Keycloak server error during login for email: {}", request.getEmail());
                    throw new KeycloakException();
                })
                .body(KeycloakTokenResponse.class);

        return new AuthenticationResult<>(
                TokenResponse.from(token),
                new CookiePayload(token.getRefreshToken())
        );
    }

    @Override
    public AuthenticationResult<TokenResponse> refresh(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", keycloakProperties.getWeb().getClientId());
        body.add("client_secret", keycloakProperties.getWeb().getClientSecret());
        body.add("refresh_token", refreshToken);

        KeycloakTokenResponse token = keycloakTokenClient.post()
                .uri("/token")
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.warn("Token refresh failed - token may be expired");
                    throw new InvalidTokenException("Invalid or expired refresh token");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Keycloak server error during refresh request");
                    throw new KeycloakException();
                })
                .body(KeycloakTokenResponse.class);

        return new AuthenticationResult<>(
                TokenResponse.from(token),
                new CookiePayload(token.getRefreshToken())
        );
    }

    @Override
    public void logout(String refreshToken, String sessionId) {
        if (revokeViaRefreshToken(refreshToken)) {
            return;
        }

        if (revokeViaSessionId(sessionId)) {
            return;
        }

        log.warn("Keycloak logout failed via both refresh token and session ID — proceeding with client-side logout only");
    }

    private boolean revokeViaRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return false;
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakProperties.getWeb().getClientId());
        body.add("client_secret", keycloakProperties.getWeb().getClientSecret());
        body.add("refresh_token", refreshToken);

        try {
            keycloakTokenClient.post()
                    .uri("/logout")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("User session revoked in Keycloak via refresh token");
            return true;
        } catch (Exception ex) {
            log.warn("Keycloak logout via refresh token failed: {}", ex.getMessage());
            return false;
        }
    }

    private boolean revokeViaSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }

        String adminToken = adminTokenService.getAdminToken();

        try {
            keycloakAdminClient.delete()
                    .uri("/sessions/" + sessionId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .toBodilessEntity();
            log.info("User session revoked in Keycloak via session ID");
            return true;
        } catch (Exception ex) {
            log.warn("Keycloak logout via session ID failed: {}", ex.getMessage());
            return false;
        }
    }

    public void logoutAllSessions(String userId) {
        String adminToken = adminTokenService.getAdminToken();

        try {
            keycloakAdminClient.post()
                    .uri("/users/" + userId + "/logout")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Revoked all sessions for user {}", userId);
        } catch (Exception ex) {
            log.error("Failed to revoke all sessions for user {}: {}", userId, ex.getMessage());
            throw new KeycloakException("Failed to log out user from all devices");
        }
    }
}