package com.AbadianoKent.Oauth2.security;

import com.AbadianoKent.Oauth2.model.AuthProvider;
import com.AbadianoKent.Oauth2.model.User;
import com.AbadianoKent.Oauth2.repository.AuthProviderRepository;
import com.AbadianoKent.Oauth2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;

    public CustomOAuth2UserService(UserRepository userRepository, AuthProviderRepository authProviderRepository) {
        this.userRepository = userRepository;
        this.authProviderRepository = authProviderRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String providerUserId = null;
        String email = null;
        String name = null;
        String avatar = null;

        if ("GOOGLE".equals(registrationId)) {
            providerUserId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            avatar = (String) attributes.get("picture");
        } else if ("GITHUB".equals(registrationId)) {
            providerUserId = attributes.get("id").toString();
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            avatar = (String) attributes.get("avatar_url");

            // GitHub may return null email in public profile; try to parse from nested structures if present
            if (email == null && attributes.containsKey("emails")) {
                // skip complex parsing here; rely on provider to supply email via scope
            }
        }

        if (email == null) {
            log.warn("OAuth2 user has no email: {} {}", registrationId, providerUserId);
            email = registrationId + "_" + providerUserId + "@noemail.local";
        }

        AuthProvider auth = authProviderRepository.findByProviderAndProviderUserId(registrationId, providerUserId)
                .orElse(null);

        User user;
        if (auth != null) {
            user = auth.getUser();
        } else {
            // Try to find by email
            user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setDisplayName(name == null ? email : name);
                user.setAvatarUrl(avatar);
                userRepository.save(user);
            }

            AuthProvider newAuth = new AuthProvider();
            newAuth.setUser(user);
            newAuth.setProvider(registrationId);
            newAuth.setProviderUserId(providerUserId);
            newAuth.setProviderEmail(email);
            authProviderRepository.save(newAuth);
        }

        // Return a simple OAuth2User with user's email as principal attribute
        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("id", user.getId(), "email", user.getEmail(), "name", user.getDisplayName()),
                "email"
        );
    }
}
