package com.mori.gateway.exception;

import com.mori.gateway.filter.MoriExchangeAttributes;
import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.response.ApiError;
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
        // originalPath does not reflect the path mutation performed by TrailingSlashHandlerFilter,
        // so the filter stores the rewritten path as an exchange attribute for us to retrieve here.
        String originalPath = exchange.getRequest().getPath().toString();
        String normalizedPath = exchange.getAttributes()
                .getOrDefault(MoriExchangeAttributes.REWRITTEN_PATH_NO_TRAILING_SLASH, originalPath)
                .toString();
        String method = exchange.getRequest().getMethod().toString();

        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());
            ErrorCode errorCode = resolveErrorCode(status);
            String message = errorCode.getDefaultMessage();

            log.warn("Gateway error [{} {}]: {}",
                    method, normalizedPath, rse.getReason());

            ApiError error = ApiError.of(errorCode, message, normalizedPath);

            return errorResponseWriter.write(exchange, error);
        }

        log.error("Unhandled gateway exception [{} {}]: {}",
                method, originalPath, ex.getMessage(), ex);

        ApiError error = ApiError.internal(normalizedPath);

        return errorResponseWriter.write(exchange, error);
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