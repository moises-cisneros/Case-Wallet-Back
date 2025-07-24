package com.case_wallet.apirest.infrastructure.security.service;

import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.database.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find by phone number first (new authentication)
        Optional<UserEntity> userOpt = userRepository.findByPhoneNumber(identifier);
        
        // If not found by phone, try by email (legacy authentication)
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }
        
        return userOpt.map(user -> {
                    log.info("Loading user details for identifier: {} with roles: {}",
                            identifier,
                            user.getRoles().stream()
                                    .map(RoleEntity::getName)
                                    .collect(Collectors.joining(", ")));
                    return user; // Return the UserEntity directly as it implements UserDetails
                })
                .orElseThrow(() -> {
                    log.error("User not found with identifier: {}", identifier);
                    return new UsernameNotFoundException("User not found with identifier: " + identifier);
                });
    }
}
