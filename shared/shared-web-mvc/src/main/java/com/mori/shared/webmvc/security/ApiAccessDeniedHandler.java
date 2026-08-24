package com.mori.shared.webmvc.security;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.response.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException, ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Access denied [{} {}]: {}", method, path, ex.getMessage());

        ApiError apiError = ApiError.of(
                ErrorCode.FORBIDDEN,
                ErrorCode.FORBIDDEN.getDefaultMessage(),
                path
        );

        errorResponseWriter.write(response, apiError);
    }
}