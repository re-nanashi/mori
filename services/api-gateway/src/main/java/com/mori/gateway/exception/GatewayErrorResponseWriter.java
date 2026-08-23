package com.mori.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mori.common.response.ApiError;
import com.mori.common.response.ApiEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayErrorResponseWriter {
    private final ObjectMapper objectMapper;

    public Mono<Void> write(ServerWebExchange exchange, ApiError apiError) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.valueOf(apiError.getStatus()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(ApiEnvelope.error(apiError));
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize error response", ex);
            return response.setComplete();
        }
    }
}