package kh.devspaceapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import kh.devspaceapi.auth.jwt.util.JwtProvider;
import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.comm.response.ApiResponse;
import kh.devspaceapi.comm.util.CookieUtil;
import kh.devspaceapi.model.dto.auth.AuthRequestDto;
import kh.devspaceapi.model.dto.auth.AuthResponseDto;
import kh.devspaceapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(HttpServletResponse response, @RequestBody AuthRequestDto request) {
        CustomUserDetails user = authService.authenticateUser(request.getUserId(), request.getPassword());

        String accessToken = jwtProvider.createToken(user.getUserId(), user.getRole(), 10 * 24 * 60 * 60 * 1000); // 10분
        String refreshToken = jwtProvider.createToken(user.getUserId(), user.getRole(), 60 * 60 * 1000); // 60분

        CookieUtil.addHttpOnlyCookie(response, "refreshToken", refreshToken);

        return ResponseEntity.ok(ApiResponse.success(AuthResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .build()
        ));
    }
}
