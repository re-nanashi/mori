package com.mori.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {
    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String originalPath = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();
        long startTime = System.currentTimeMillis();

        // inject X-Original-Path before rewrite happens; this is so that API error messages would output the
        // original path instead of the stripped down version of the path (e.g., /api/v1/users instead of /users)
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.remove("X-Original-Path" )) // strip spoofed
                .header("X-Original-Path", originalPath)    // inject real
                .build();

        // pre-filter execution (before passing to microservices)
        log.info("[GATEWAY] Request: {} {}", method, originalPath);

        // calling the next filter in the chain
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;

                    // post-filter execution (after receiving response from microservices)
                    log.info("[GATEWAY] Response: {} {} {}ms",
                            exchange.getResponse().getStatusCode(),
                            exchange.getRequest().getPath(),
                            duration
                    );
        }));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}