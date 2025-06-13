package com.case_wallet.apirest.application.auth.port.in;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.LoginRequestDTO;

public interface AuthenticateUserUseCase {
    AuthResponseDTO authenticate(LoginRequestDTO request);
}
