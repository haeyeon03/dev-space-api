package kh.devspaceapi.auth.oauth2.handler;

import kh.devspaceapi.auth.oauth2.exception.OAuth2UserNotFoundException;
import kh.devspaceapi.comm.util.OAuth2TempStore;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if (exception instanceof OAuth2UserNotFoundException oAuth2User) {
            String key = UUID.randomUUID().toString();
            OAuth2TempStore.put(key, oAuth2User.getName(), oAuth2User.getEmail(), "", "", ""); // 이메일, 닉네임 포함
            response.sendRedirect("http://localhost:5173/oauth2/callback/failure?key=" + key);

        } else if (exception instanceof OAuth2AuthenticationException oAuth2Authentication) {
            String errorMessage = oAuth2Authentication.getError().getDescription();
            response.sendRedirect("http://localhost:5173/oauth2/callback/failure?error="
                    + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        } else {
            // 일반 OAuth2 오류
            response.sendRedirect("http://localhost:5173/oauth2/callback/failure?error=unknown");
        }
    }
}
