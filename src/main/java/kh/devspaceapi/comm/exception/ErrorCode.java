package kh.devspaceapi.comm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMPTY_USER_ID("USER001", "User ID 값이 존재하지 않습니다"),
    EMPTY_PASSWORD("USER002", "Password 값이 존재하지 않습니다"),
    EMPTY_NICKNAME("USER003", "Nickname 값이 존재하지 않습니다"),
    EMPTY_GENDER("USER004", "Gender 값이 존재하지 않습니다"),
    EMPTY_BIRTHDATE("USER005", "Birthdate 값이 존재하지 않습니다"),

    NO_EXIST_NEWS_POST("NEWS001", "News Post ID 값이 존재하지 않습니다"),

    DUPLICATED_USER("USER006", "이미 존재하는 User ID 입니다."),
    UNAUTHORIZED_USER("USER007", "사용자 인증을 실패하였습니다."),

    JWT_NOT_FOUND("JWT001", "토큰이 존재하지 않습니다."),
    JWT_EXPIRED("JWT002", "만료된 토큰입니다."),
    JWT_UNSUPPORTED("JWT003", "지원하지 않는 JWT 형식입니다."),
    JWT_MALFORMED("JWT004", "잘못된 형식의 JWT입니다."),
    JWT_UNKNOWN_ERROR("JWT005", "JWT 처리 중 알 수 없는 오류가 발생했습니다."),

    AUTH_UNAUTHORIZED("AUTH001", "인증되지 않은 요청입니다")
    ;


    private final String code;
    private final String message;

}
