package com.mori.common.exception;

import com.mori.common.error.ErrorCode;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}