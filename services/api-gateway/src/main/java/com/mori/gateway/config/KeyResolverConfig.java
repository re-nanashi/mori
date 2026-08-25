package com.mori.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class KeyResolverConfig {
    @Primary
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("anonymous");
    }

    @Bean
    @Profile("dev")
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                exchange.getRequest()
                        .getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .defaultIfEmpty("unknown");
    }

    @Bean("ipKeyResolver")
    @Profile("prod")
    public KeyResolver ipKeyResolverProd() {
        return exchange -> {
            String forwardedFor = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Forwarded-For");

            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                String clientIp = forwardedFor.split(",")[0].trim();
                return Mono.just(clientIp);
            }

            return Mono.justOrEmpty(
                    exchange.getRequest().getRemoteAddress())
                    .map(addr -> addr.getAddress().getHostAddress())
                    .defaultIfEmpty("unknown");
        };
    }
}