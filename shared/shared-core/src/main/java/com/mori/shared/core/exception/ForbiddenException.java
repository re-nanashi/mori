package com.mori.shared.core.exception;

import com.mori.shared.core.error.ErrorCode;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}