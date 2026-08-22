package com.mori.gateway.exception;

import com.mori.common.error.ErrorCode;
import com.mori.common.response.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAccessDeniedHandler implements ServerAccessDeniedHandler {
    private final GatewayErrorResponseWriter errorResponseWriter;

    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();

        log.warn("Access denied [{} {}]: {}", method, path, ex.getMessage());

        ApiError apiError = ApiError.of(
                ErrorCode.FORBIDDEN,
                ErrorCode.FORBIDDEN.getDefaultMessage(),
                path
        );

        return errorResponseWriter.write(exchange, apiError);
    }
}