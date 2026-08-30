package com.mori.shared.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mori.shared.core.error.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "error_code",
        "message",
        "path",
        "timestamp",
        "details",
        "trace_id"
})
public class ApiError {
    private int status;
    @JsonProperty("error_code")
    private String errorCode;
    private String message;
    private String path;
    private Instant timestamp;
    private Map<String, List<String>> details;
    @JsonProperty("trace_id")
    private String traceId;

    public static ApiError of(ErrorCode errorCode, String path) {
        return ApiError.builder()
                .status(errorCode.getStatus().value())
                .errorCode(errorCode.getCode())
                .message(errorCode.getDefaultMessage())
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiError of(ErrorCode errorCode, String message, String path) {
        return ApiError.builder()
                .status(errorCode.getStatus().value())
                .errorCode(errorCode.getCode())
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiError of(ErrorCode errorCode, String message, String path, Map<String, List<String>> details) {
        return ApiError.builder()
                .status(errorCode.getStatus().value())
                .errorCode(errorCode.getCode())
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }

    public static ApiError validation(String path, Map<String, List<String>> details) {
        return ApiError.builder()
                .status(ErrorCode.VALIDATION_ERROR.getStatus().value())
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }

    public static ApiError internal(String path) {
        return ApiError.builder()
                .status(ErrorCode.INTERNAL_ERROR.getStatus().value())
                .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
                .message("An unexpected error occurred")
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}