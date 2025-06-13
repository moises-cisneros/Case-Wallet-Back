package com.case_wallet.apirest.application.user.port.in;

import com.case_wallet.apirest.application.user.dto.UserProfileDTO;

import java.util.UUID;

public interface GetUserProfileUseCase {
    UserProfileDTO getUserProfile(UUID userId);
} 