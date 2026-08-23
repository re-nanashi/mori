package com.mori.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "message",
        "data",
        "error",
        "page"
})
public class ApiEnvelope<T> {
    private boolean success;
    private String message;
    private T data;
    private ApiError error;
    private PageMeta page;

    // Success factories
    public static <T> ApiEnvelope<T> ok(T data) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiEnvelope<T> ok(T data, String message) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiEnvelope<T> created(T data) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .message("Resource created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiEnvelope<T> created(T data, String message) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiEnvelope<T> paged(T data, PageMeta page) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .data(data)
                .page(page)
                .build();
    }

    // Error factory
    public static <T> ApiEnvelope<T> error(ApiError error) {
        return ApiEnvelope.<T>builder()
                .success(false)
                .error(error)
                .build();
    }
}