package com.mori.shared.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Generic
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"),
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST,
            "Validation failed"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE,
            "Service temporarily unavailable"),
    GATEWAY_TIMEOUT("GATEWAY_TIMEOUT", HttpStatus.GATEWAY_TIMEOUT,
            "The upstream service did not response in time"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND,
            "Resource not found"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED,
            "Authentication required"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN,
            "Insufficient permissions"),
    CONFLICT("CONFLICT", HttpStatus.CONFLICT,
            "Resource already exists"),

    // HTTP/Route errors
    ROUTE_NOT_FOUND("ROUTE_NOT_FOUND", HttpStatus.NOT_FOUND,
            "The requested endpoint does not exist"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED,
            "HTTP method not supported for this endpoint"),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "Media type not supported"),
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST,
            "Malformed or missing request parameters"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS,
            "Too many requests"),

    // Auth
    AUTH_INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED,
            "Invalid email or password"),
    AUTH_EMAIL_EXISTS("AUTH_EMAIL_EXISTS", HttpStatus.CONFLICT,
            "Email already in use"),
    AUTH_INVALID_TOKEN("AUTH_INVALID_TOKEN", HttpStatus.UNAUTHORIZED,
            "Invalid or expired token"),
    AUTH_KEYCLOAK_UNAVAILABLE("AUTH_KEYCLOAK_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE,
            "Authentication service unavailable"),

    // User
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND,
            "User not found"),
    USER_USERNAME_EXISTS("USER_USERNAME_EXISTS", HttpStatus.CONFLICT,
            "Username already taken"),

    // Post
    POST_NOT_FOUND("POST_NOT_FOUND", HttpStatus.NOT_FOUND,
            "Post not found"),
    POST_UNAUTHORIZED_EDIT("POST_UNAUTHORIZED_EDIT", HttpStatus.FORBIDDEN,
            "You are not the owner of this post");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;
}