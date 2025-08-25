package kh.devspaceapi.comm.util;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

// Oauth2 인증 완료된 사용자 정보를 저장한 임시 저장소 (멀티 서버로 운영할 경우 DB로 전환 필요)
public class OAuth2TempStore {
    public static final ConcurrentHashMap<String, UserInfo> STORE = new ConcurrentHashMap<>();

    public static void put(String key, String nickname, String email, String role, String accessToken, String refreshToken) {
        STORE.put(key, new UserInfo(nickname, email, role, accessToken, refreshToken));
    }
    public static UserInfo get(String key) {
        return STORE.get(key);
    }
    public static void remove(String key) {
        STORE.remove(key);
    }


    @Getter
    @Setter
    public static class UserInfo {
        private String nickname;
        private String email;
        private String role;
        private String accessToken;
        private String refreshToken;

        // 성공 시
        UserInfo(String nickname, String email, String role, String accessToken, String refreshToken) {
            this.nickname = nickname;
            this.email = email;
            this.role = role;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;

        }

        // 실패 시
        UserInfo(String email) {
            this.email = email;
        }
    }
}
