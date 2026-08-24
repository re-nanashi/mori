package com.mori.shared.core.exception;

import com.mori.shared.core.error.ErrorCode;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}