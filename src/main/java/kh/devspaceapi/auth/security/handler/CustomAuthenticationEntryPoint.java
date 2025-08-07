package kh.devspaceapi.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.comm.exception.JwtException;
import kh.devspaceapi.comm.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Object jwtExObj = request.getAttribute("JWT_EXCEPTION");

        if (jwtExObj instanceof JwtException e) {
            writeErrorResponse(response, e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } else {
            writeErrorResponse(response, ErrorCode.AUTH_UNAUTHORIZED.getCode(), ErrorCode.AUTH_UNAUTHORIZED.getMessage());
        }
    }

    private void writeErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        ApiResponse<Void> errorResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), code, message);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
