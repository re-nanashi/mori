package com.mori.common.advice;

import com.mori.common.error.ErrorCode;
import com.mori.common.exception.BaseException;
import com.mori.common.exception.RateLimitExceededException;
import com.mori.common.response.ApiError;
import com.mori.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 4xx: Business exceptions
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBase(
            BaseException ex,
            HttpServletRequest request
    ) {
        log.warn("Business exception [{}]: {}", ex.getErrorCode().getCode(), ex.getMessage());

        ApiError error = ApiError.of(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ApiResponse.error(error));
    }

    // 400: Validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        log.debug("Validation failed at {}: {}", request.getRequestURI(), details);

        ApiError error = ApiError.validation(request.getRequestURI(), details);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    // 404: Route doesn't exist in API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        log.debug("Route not found: {}", request.getRequestURI());

        ApiError error = ApiError.of(
                ErrorCode.ROUTE_NOT_FOUND,
                "No endpoint found for: " + request.getMethod() + " " + request.getRequestURI(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }

    // 405: HTTP method not allowed (e.g., POST on GET-only endpoint)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.debug("Method not allowed: {} {}", ex.getMethod(), request.getRequestURI());

        ApiError error = ApiError.of(
                ErrorCode.METHOD_NOT_ALLOWED,
                ex.getMethod() + " method is not supported for this endpoint",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(error));
    }

    // 415: Unsupported media type (e.g. sending XML to a JSON endpoint)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.debug("Unsupported media type: {}", ex.getContentType());

        ApiError error = ApiError.of(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Media type " + ex.getContentType() + " is not supported",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error(error));
    }

    // 400: Malformed JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.debug("Malformed request body at {}", request.getRequestURI());

        ApiError error = ApiError.of(
                ErrorCode.MALFORMED_REQUEST,
                "Request body is malformed or missing",
                request.getRequestURI()
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    // 400: Missing or wrong type path/query params
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.debug("Type mismatch for param '{}': {}", ex.getName(), ex.getValue());

        ApiError error = ApiError.of(
                ErrorCode.VALIDATION_ERROR,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'",
                request.getRequestURI()
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    // 429: Too many requests (if not handled by gateway)
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(
            RateLimitExceededException ex, HttpServletRequest request) {
        log.warn("Rate limit exceeded at {}", request.getRequestURI());
        ApiError error = ApiError.of(
                ErrorCode.RATE_LIMIT_EXCEEDED,
                "Too many requests — please try again later",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(error));
    }

    // 500: Fallback for all uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = ApiError.internal(request.getRequestURI());

        return ResponseEntity.internalServerError()
                .body(ApiResponse.error(error));
    }
}