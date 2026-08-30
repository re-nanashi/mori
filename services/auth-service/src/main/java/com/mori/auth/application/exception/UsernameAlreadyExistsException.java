package com.mori.auth.application.exception;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.exception.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException() {
        super(ErrorCode.USER_USERNAME_EXISTS.getDefaultMessage(), ErrorCode.USER_USERNAME_EXISTS);
    }

    public UsernameAlreadyExistsException(String message) {
        super(message, ErrorCode.USER_USERNAME_EXISTS);
    }
}