package com.mori.auth.application.exception;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super(ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage(), ErrorCode.AUTH_INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException(String message) {
        super(message, ErrorCode.AUTH_INVALID_CREDENTIALS);
    }
}