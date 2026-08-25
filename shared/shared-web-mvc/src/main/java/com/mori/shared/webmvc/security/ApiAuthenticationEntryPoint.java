package com.mori.shared.webmvc.security;

import com.mori.shared.core.error.ErrorCode;
import com.mori.shared.core.response.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException, ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Unauthorized access [{} {}]: {}", method, path, ex.getMessage());

        ApiError error = ApiError.of(
                ErrorCode.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED.getDefaultMessage(),
                path
        );

        errorResponseWriter.write(response, error);
    }
}