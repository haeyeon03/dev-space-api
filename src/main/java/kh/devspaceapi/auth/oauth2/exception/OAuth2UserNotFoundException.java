package kh.devspaceapi.auth.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2UserNotFoundException extends AuthenticationException {

    private final String email;
    private final String name;

    public OAuth2UserNotFoundException(String email, String name) {
        super("OAuth2 user not found in our system");
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
