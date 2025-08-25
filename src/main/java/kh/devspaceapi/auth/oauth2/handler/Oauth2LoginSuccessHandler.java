package kh.devspaceapi.auth.oauth2.handler;

import kh.devspaceapi.auth.jwt.util.JwtProvider;
import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.comm.util.OAuth2TempStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiry;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();
        if(!(principal instanceof CustomUserDetails)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user details");
            return;
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        String accessToken = jwtProvider.createToken(
                userDetails.getUserId(),
                userDetails.getRole(),
                accessTokenExpiry
        );
        String refreshToken = jwtProvider.createToken(
                userDetails.getUserId(),
                userDetails.getRole(),
                refreshTokenExpiry
        );



        String key = UUID.randomUUID().toString();
        OAuth2TempStore.put(key, userDetails.getNickname(),userDetails.getEmail(),userDetails.getRole(),accessToken,refreshToken);

        // 프론트로 리다이렉트
        response.sendRedirect("http://localhost:5173/oauth2/callback/success?key=" + key);
    }
}
