package com.case_wallet.apirest.infrastructure.security.service;

import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomOAuth2User extends DefaultOAuth2User {
    @Getter
    private final String userId;
    private final Set<RoleEntity> roles;

    public CustomOAuth2User(OAuth2User oAuth2User, String userId, Set<RoleEntity> roles) {
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "name");
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
} 