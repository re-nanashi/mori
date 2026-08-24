package com.mori.shared.core.exception;

import com.mori.shared.core.error.ErrorCode;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}