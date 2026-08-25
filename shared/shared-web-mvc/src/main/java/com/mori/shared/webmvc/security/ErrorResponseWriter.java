package com.mori.shared.webmvc.security;

import com.mori.shared.core.response.ApiEnvelope;
import com.mori.shared.core.response.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class ErrorResponseWriter {
    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ApiError error) {
        response.setStatus(error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            objectMapper.writeValue(response.getWriter(), ApiEnvelope.error(error));
        } catch (IOException ex) {
            log.error("Failed to serialize error response", ex);
        }
    }
}