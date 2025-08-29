package kh.devspaceapi.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {
    private String userId;
    private String password;
    private String nickname;
    private String email;
    private String role;
    private boolean active;

    public CustomUserDetails(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    };

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }
}
