package com.mori.common.exception;

import com.mori.common.error.ErrorCode;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}