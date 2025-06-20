package com.case_wallet.apirest.infrastructure.security.service;

import com.case_wallet.apirest.common.exception.ResourceNotFoundException;
import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.database.user.repository.JpaUserRepository;
import com.case_wallet.apirest.infrastructure.database.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JpaUserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getName(); // Google's user ID is usually the 'sub' claim, accessible via getName()

        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user details if necessary
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
            }
            // Link Google ID if not already linked
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
            }
            user = userRepository.save(user);
        } else {
            // Register new user
            user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setGoogleId(googleId);
            user.setEnabled(true);

            // Assign default role, e.g., "USER"
            RoleEntity userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: USER"));
            user.setRoles(Collections.singleton(userRole));
            user = userRepository.save(user);
        }

        // Return an OAuth2User that contains our internal user ID and roles
        // This is crucial for Spring Security to build the Authentication object
        return new CustomOAuth2User(oAuth2User, user.getId().toString(), user.getRoles());
    }
} 