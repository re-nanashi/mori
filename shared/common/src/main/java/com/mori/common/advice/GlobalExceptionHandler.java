package com.mori.common.advice;

import com.mori.common.error.ErrorCode;
import com.mori.common.exception.BaseException;
import com.mori.common.exception.RateLimitExceededException;
import com.mori.common.response.ApiError;
import com.mori.common.response.ApiEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {
    // Handle business exceptions (4xx)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleBase(BaseException ex, HttpServletRequest request) {
        log.warn("Business exception [{}]: {}", ex.getErrorCode().getCode(), ex.getMessage());

        ApiError error = ApiError.of(ex.getErrorCode(), ex.getMessage(), resolvePath(request));

        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ApiEnvelope.error(error));
    }

    // Handle validation exception (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        log.debug("Validation failed at {}: {}", path, details);

        ApiError error = ApiError.validation(path, details);

        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(error));
    }

    // Handle non-existent routes (404)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        log.debug("Route not found: {}", path);

        ApiError error = ApiError.of(
                ErrorCode.ROUTE_NOT_FOUND,
                "The requested endpoint does not exist",
                path
        );

        return ResponseEntity.status(ErrorCode.ROUTE_NOT_FOUND.getStatus())
                .body(ApiEnvelope.error(error));
    }

    // Handle HTTP method not allowed (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        log.debug("Method not allowed: {} {}", ex.getMethod(), path);

        ApiError error = ApiError.of(
                ErrorCode.METHOD_NOT_ALLOWED,
                ex.getMethod() + " method is not supported for this endpoint",
                path
        );

        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ApiEnvelope.error(error));
    }

    // Handle unsupported media types (415)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.debug("Unsupported media type: {}", ex.getContentType());

        ApiError error = ApiError.of(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Media type " + ex.getContentType() + " is not supported",
                resolvePath(request)
        );

        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus())
                .body(ApiEnvelope.error(error));
    }

    // Handle invalid or malformed request (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        log.debug("Malformed request body at {}", path);

        ApiError error = ApiError.of(
                ErrorCode.INVALID_REQUEST,
                "Request body is malformed or missing",
                path
        );

        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(error));
    }

    // Handle missing or wrong type path/query params (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.debug("Type mismatch for param '{}': {}", ex.getName(), ex.getValue());

        ApiError error = ApiError.of(
                ErrorCode.VALIDATION_ERROR,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'",
                resolvePath(request)
        );

        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(error));
    }

    // Handle too many requests (429)
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleRateLimit(RateLimitExceededException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        // TODO: include clientId and IpAddress
        log.warn("Rate limit exceeded [{} {}]: {}",
                request.getMethod(), path, ex.getMessage());

        ApiError error = ApiError.of(
                ErrorCode.RATE_LIMIT_EXCEEDED,
                "Too many requests. Please try again later",
                path
        );

        return ResponseEntity.status(ErrorCode.RATE_LIMIT_EXCEEDED.getStatus())
                .body(ApiEnvelope.error(error));
    }

    // Handle missing cookie exception (400)
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMissingCookie(MissingRequestCookieException ex, HttpServletRequest request) {
        String path = resolvePath(request);

        String message = "refresh_token".equals(ex.getCookieName()) ?
                "Refresh token is missing" :
                "Required cookie is missing";

        log.debug("Missing required cookie '{}' at {}", ex.getCookieName(), path);

        ApiError error = ApiError.of(
                ErrorCode.INVALID_REQUEST,
                message,
                path
        );

        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(error));
    }

    // Fallback for all uncaught exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        String path = resolvePath(request);

        log.error("Unhandled exception [{} {}]: {}", request.getMethod(), path, ex.getMessage(), ex);

        ApiError error = ApiError.internal(path);

        return ResponseEntity.internalServerError()
                .body(ApiEnvelope.error(error));
    }

    // Helper method that resolves the original request path. The API gateway includes the original path
    // in the request headers, allowing the response to preserve the path originally requested by the client
    // even after the gateway reroutes the request to a route without the API version.
    private String resolvePath(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Original-Path"))
                .orElse(request.getRequestURI());
    }
}