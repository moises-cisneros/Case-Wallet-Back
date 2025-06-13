package com.case_wallet.apirest.application.user.port.in;

import com.case_wallet.apirest.application.user.dto.UpdateUserProfileDTO;
import com.case_wallet.apirest.application.user.dto.UserProfileDTO;

public interface UpdateUserProfileUseCase {
    UserProfileDTO updateProfile(UpdateUserProfileDTO updateUserProfileDTO);
}