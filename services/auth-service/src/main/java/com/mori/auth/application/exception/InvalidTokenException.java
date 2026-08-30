package com.mori.auth.application.exception;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.exception.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {
    public InvalidTokenException() {
        super(ErrorCode.AUTH_INVALID_TOKEN.getDefaultMessage(), ErrorCode.AUTH_INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(message, ErrorCode.AUTH_INVALID_TOKEN);
    }
}