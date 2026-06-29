package com.mori.common.exception;

import com.mori.common.error.ErrorCode;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}