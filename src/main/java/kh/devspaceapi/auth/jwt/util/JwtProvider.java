package kh.devspaceapi.auth.jwt.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.comm.exception.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Log4j2
@Component
public class JwtProvider {
    private String SECRET_KEY = "2025-kh-dev-space-team-final-project-with-spring-boot";
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 bytes) for HS256.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String userId, String role, long tokenExpirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenExpirationMs);
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException(ErrorCode.JWT_NOT_FOUND);
        }
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        } catch (Exception e) {
            log.error("Unknown JWT parsing error", e);
            throw new JwtException(ErrorCode.JWT_UNKNOWN_ERROR);
        }
    }
}
