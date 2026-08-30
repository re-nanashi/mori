package com.mori.auth.infra.keycloak;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.exception.BaseException;

public class KeycloakException extends BaseException {
    public KeycloakException() {
        super(ErrorCode.AUTH_KEYCLOAK_UNAVAILABLE.getDefaultMessage(), ErrorCode.AUTH_KEYCLOAK_UNAVAILABLE);
    }

    public KeycloakException(String message) {
        super(message, ErrorCode.AUTH_KEYCLOAK_UNAVAILABLE);
    }
}