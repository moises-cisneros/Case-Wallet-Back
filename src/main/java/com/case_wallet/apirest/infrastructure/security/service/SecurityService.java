package com.case_wallet.apirest.infrastructure.security.service;

import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserRepositoryPort userRepositoryPort;

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        String username = authentication.getName();
        
        // Try to find user by phone number first (new auth system)
        User user = userRepositoryPort.findByPhoneNumber(username)
                .orElse(null);
        
        // If not found by phone, try by email (legacy auth system)
        if (user == null) {
            user = userRepositoryPort.findByEmail(username)
                    .orElse(null);
        }
        
        if (user == null) {
            throw new IllegalStateException("Usuario no encontrado: " + username);
        }
        
        return user.getId();
    }

    public User getCurrentUser() {
        UUID userId = getCurrentUserId();
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
