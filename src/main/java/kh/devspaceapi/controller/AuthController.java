package kh.devspaceapi.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.devspaceapi.auth.jwt.util.JwtProvider;
import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.comm.exception.JwtException;
import kh.devspaceapi.comm.response.ApiResponse;
import kh.devspaceapi.comm.util.CookieUtil;
import kh.devspaceapi.model.dto.auth.AuthRequestDto;
import kh.devspaceapi.model.dto.auth.AuthResponseDto;
import kh.devspaceapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    private final String REFRESH_TOKEN_KEY = "refreshToken";

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiry;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiry;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(HttpServletResponse response, @RequestBody AuthRequestDto request) {
        CustomUserDetails user = authService.authenticateUser(request.getUserId(), request.getPassword());

        String accessToken = jwtProvider.createToken(user.getUserId(), user.getRole(), accessTokenExpiry);
        String refreshToken = jwtProvider.createToken(user.getUserId(), user.getRole(), refreshTokenExpiry);
        CookieUtil.addHttpOnlyCookie(response, "refreshToken", refreshToken);

        return ResponseEntity.ok(ApiResponse.success(AuthResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .build()
        ));
    }

    /**
     * Refresh Token을 이용한 Access Token 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. 쿠키에서 Refresh Token 가져오기
        String refreshToken = CookieUtil.getCookieValue(request, REFRESH_TOKEN_KEY);

        // 2. Refresh Token 검증
        Claims claims;
        try {
            claims = jwtProvider.parseToken(refreshToken);
        } catch (JwtException e) {
            // 토큰이 없거나 만료/변조 → 강제 로그아웃 유도
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
        }

        String userId = claims.get("userId", String.class);
        String role = claims.get("role", String.class);

        // 3. 새 Access/Refresh Token 발급
        String newAccessToken = jwtProvider.createToken(userId, role, accessTokenExpiry);
        String newRefreshToken = jwtProvider.createToken(userId, role, refreshTokenExpiry);

        // 4. Refresh Token을 HttpOnly 쿠키로 갱신
        CookieUtil.addHttpOnlyCookie(response, REFRESH_TOKEN_KEY, newRefreshToken);

        // 5. Access Token은 body로 반환
        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }

    /**
     * 로그아웃 (쿠키 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response) {
        CookieUtil.deleteCookie(response, "refreshToken");
        return ResponseEntity.ok(ApiResponse.success("LOGOUT_SUCCESS", null));
    }
}
