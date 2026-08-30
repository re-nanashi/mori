package com.mori.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CookiePayload {
    private String refreshToken;
    // TODO: deviceId; Multi-device sign in
}