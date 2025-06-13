package com.case_wallet.apirest.application.auth.port.in;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.RegisterRequestDTO;

public interface RegisterUserUseCase {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO registerAdmin(RegisterRequestDTO request);
}
