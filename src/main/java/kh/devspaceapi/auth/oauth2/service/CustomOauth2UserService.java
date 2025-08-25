package kh.devspaceapi.auth.oauth2.service;

import kh.devspaceapi.auth.oauth2.exception.OAuth2UserNotFoundException;
import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.repository.UsersRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository usersRepository;

    public CustomOauth2UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if(email == null) {
            throw new OAuth2AuthenticationException("OAuth2 provider did not provide an email");
        }

        return usersRepository.findByEmail(email)
                .map(user -> new CustomUserDetails(
                        user.getUserId(),
                        user.getPassword(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getRole()
                ))
                .orElseThrow(() -> new OAuth2UserNotFoundException(email, name));
    }
}
