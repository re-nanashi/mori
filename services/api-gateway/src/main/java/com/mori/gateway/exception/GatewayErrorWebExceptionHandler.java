package com.mori.gateway.exception;

import com.mori.common.error.ErrorCode;
import com.mori.common.response.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private final GatewayErrorResponseWriter errorResponseWriter;

    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();

        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());
            ErrorCode errorCode = resolveErrorCode(status);
            String message = errorCode.getDefaultMessage();

            log.warn("Gateway error [{} {}]: {}",
                    method, path, rse.getReason());

            ApiError apiError = ApiError.of(errorCode, message, path);
            return errorResponseWriter.write(exchange, apiError);
        }

        log.error("Unhandled gateway exception [{} {}]: {}",
                method, path, ex.getMessage(), ex);

        ApiError apiError = ApiError.internal(path);
        return errorResponseWriter.write(exchange, apiError);
    }

    private ErrorCode resolveErrorCode(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> ErrorCode.ROUTE_NOT_FOUND;
            case METHOD_NOT_ALLOWED -> ErrorCode.METHOD_NOT_ALLOWED;
            case UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case FORBIDDEN -> ErrorCode.FORBIDDEN;
            case TOO_MANY_REQUESTS -> ErrorCode.RATE_LIMIT_EXCEEDED;
            case SERVICE_UNAVAILABLE, BAD_GATEWAY -> ErrorCode.SERVICE_UNAVAILABLE;
            case GATEWAY_TIMEOUT -> ErrorCode.GATEWAY_TIMEOUT;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}