package com.mori.common.exception;

import com.mori.common.error.ErrorCode;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}