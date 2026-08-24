package com.mori.shared.core.exception;

import com.mori.shared.core.error.ErrorCode;

public class RateLimitExceededException extends BaseException {
    public RateLimitExceededException() {
        super(ErrorCode.RATE_LIMIT_EXCEEDED.getDefaultMessage(), ErrorCode.RATE_LIMIT_EXCEEDED);
    }

    public RateLimitExceededException(String message) {
        super(message, ErrorCode.RATE_LIMIT_EXCEEDED);
    }
}