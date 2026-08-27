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

            // In some cases, the newPath isn't reflected on the exchange object. Therefore, we store the rewritten
            // path as an exchange attribute so it can be retrieved later during error handling, allowing users to
            // see the URI that was actually handled by our system.
            exchange.getAttributes()
                    .put(MoriExchangeAttributes.REWRITTEN_PATH_NO_TRAILING_SLASH, newPath);

            return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build()
            );
        }

        return chain.filter(exchange);
    }
}