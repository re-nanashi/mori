package com.mori.auth.application.service;

import com.mori.auth.api.dto.*;
import com.mori.auth.application.dto.AuthenticationResult;

public interface AuthenticationService {
    AuthenticationResult<TokenResponse> register(RegisterRequest registerRequest);
    AuthenticationResult<TokenResponse> login(LoginRequest loginRequest);
    AuthenticationResult<TokenResponse> refresh(String refreshToken);
    void logout(String refreshToken, String sessionId);
    void logoutAllSessions(String userId);
}