package com.mori.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResult<T> {
    private T response;
    private CookiePayload cookies;
}