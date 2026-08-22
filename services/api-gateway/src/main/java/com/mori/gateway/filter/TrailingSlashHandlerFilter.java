package com.mori.gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrailingSlashHandlerFilter implements WebFilter {
    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // strip trailing slash if present and path is not just "/"
        if (path.length() > 1 && path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .path(newPath)
                    .build();

            return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build()
            );
        }

        return chain.filter(exchange);
    }
}