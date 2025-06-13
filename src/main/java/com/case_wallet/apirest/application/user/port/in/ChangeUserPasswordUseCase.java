package com.case_wallet.apirest.application.user.port.in;

import com.case_wallet.apirest.application.user.dto.ChangePasswordDTO;

import java.util.UUID;

public interface ChangeUserPasswordUseCase {
    void changePassword(UUID userId, ChangePasswordDTO changePasswordDTO);
}