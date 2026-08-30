package com.mori.auth.api.controller;

import com.mori.auth.api.dto.LoginRequest;
import com.mori.auth.api.dto.TokenResponse;
import com.mori.auth.api.dto.RegisterRequest;
import com.mori.auth.application.dto.AuthenticationResult;
import com.mori.auth.application.service.AuthenticationService;
import com.mori.auth.infra.security.CookieUtil;
import com.mori.shared.core.response.ApiEnvelope;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiEnvelope<TokenResponse>> register(@RequestBody @Valid RegisterRequest request) {
        AuthenticationResult<TokenResponse> result = authenticationService.register(request);
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(result.getCookies().getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiEnvelope.created(result.getResponse(), "Account created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthenticationResult<TokenResponse> result = authenticationService.login(request);
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(result.getCookies().getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiEnvelope.ok(result.getResponse(), "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiEnvelope<TokenResponse>> refresh(
            @CookieValue(name = CookieUtil.REFRESH_COOKIE_NAME) String refreshToken
    ) {
        AuthenticationResult<TokenResponse> result = authenticationService.refresh(refreshToken);
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(result.getCookies().getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiEnvelope.ok(result.getResponse(), "Refresh token rotated successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiEnvelope<Void>> logout(
            @CookieValue(name = CookieUtil.REFRESH_COOKIE_NAME, required = false) String refreshToken,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String sessionId = jwt.getClaimAsString("sid");
        authenticationService.logout(refreshToken, sessionId);
        ResponseCookie refreshTokenCookie = CookieUtil.clearRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiEnvelope.ok(null, "Logout successful"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiEnvelope<Void>> logoutAllDevices(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        authenticationService.logoutAllSessions(userId);
        ResponseCookie refreshTokenCookie = CookieUtil.clearRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiEnvelope.ok(null, "Logout successful"));
    }
}