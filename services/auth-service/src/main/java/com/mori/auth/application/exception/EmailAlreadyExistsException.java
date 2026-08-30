package com.mori.auth.application.exception;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException() {
        super(ErrorCode.AUTH_EMAIL_EXISTS.getDefaultMessage(), ErrorCode.AUTH_EMAIL_EXISTS);
    }

    public EmailAlreadyExistsException(String message) {
        super(message, ErrorCode.AUTH_EMAIL_EXISTS);
    }
}