package com.case_wallet.apirest.application.user.usecases;

import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import com.case_wallet.apirest.application.user.mapper.UserMapper;
import com.case_wallet.apirest.application.user.port.in.GetUserProfileUseCase;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    public UserProfileDTO getUserProfile(UUID userId) {
        return userRepositoryPort.findById(userId)
                .map(userMapper::toUserProfileDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
} 