package com.case_wallet.apirest.application.wallet.port.in;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;

import java.util.UUID;

public interface GenerateWalletUseCase {
    AuthResponseDTO generateWallet(UUID userId);
} 