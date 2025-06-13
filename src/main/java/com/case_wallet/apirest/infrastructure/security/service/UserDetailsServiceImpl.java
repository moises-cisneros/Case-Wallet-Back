package com.case_wallet.apirest.infrastructure.security.service;

import com.case_wallet.apirest.infrastructure.database.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    log.info("Loading user details for phone number: {} with role: {}",
                            phoneNumber,
                            user.getRole().name());
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("User not found with phone number: {}", phoneNumber);
                    return new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
                });
    }
}
