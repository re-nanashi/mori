package com.mori.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mori.common.error.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String errorCode;
    private String message;
    private int status;
    private String path;
    private Instant timestamp;
    private List<String> details;
    private String traceId;

    public static ApiError of(ErrorCode errorCode, String path) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .message(errorCode.getDefaultMessage())
                .status(errorCode.getStatus().value())
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiError of(ErrorCode errorCode, String message, String path) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .message(message)
                .status(errorCode.getStatus().value())
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiError of (ErrorCode errorCode, String message, String path, List<String> details) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .message(message)
                .status(errorCode.getStatus().value())
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }

    public static ApiError validation(String path, List<String> details) {
        return ApiError.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }

    public static ApiError internal(String path) {
        return ApiError.builder()
                .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
                .message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}