package kh.devspaceapi.service;

import kh.devspaceapi.auth.security.CustomUserDetails;

public interface AuthService {
    CustomUserDetails authenticateUser(String userId, String password);
}
