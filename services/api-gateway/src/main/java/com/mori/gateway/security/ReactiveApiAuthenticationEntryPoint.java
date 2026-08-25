package com.mori.gateway.security;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.response.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveApiAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    private final ReactiveErrorResponseWriter errorResponseWriter;

    @Override
    public @NonNull Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();

        log.warn("Unauthorized access [{} {}]: {}", method, path, ex.getMessage());

        ApiError error = ApiError.of(
                ErrorCode.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED.getDefaultMessage(),
                path
        );

        return errorResponseWriter.write(exchange, error);
    }
}