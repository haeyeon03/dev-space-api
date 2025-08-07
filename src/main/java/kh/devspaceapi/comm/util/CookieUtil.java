package kh.devspaceapi.comm.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final int DEFAULT_MAX_AGE = 24 * 60 * 60; // 1 일

    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경일 경우 true 권장
        cookie.setPath("/");
        cookie.setMaxAge(DEFAULT_MAX_AGE);
        response.addCookie(cookie);
    }
}
