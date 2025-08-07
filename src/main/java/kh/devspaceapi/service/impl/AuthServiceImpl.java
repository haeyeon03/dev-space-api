package kh.devspaceapi.service.impl;

import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.comm.exception.BusinessException;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public CustomUserDetails authenticateUser(String userId, String password) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.EMPTY_USER_ID);
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException(ErrorCode.EMPTY_PASSWORD);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
            return (CustomUserDetails) authentication.getPrincipal();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}
