package com.case_wallet.apirest.application.wallet.usecases;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.application.wallet.dto.WalletCreationDetails;
import com.case_wallet.apirest.application.wallet.port.in.GenerateWalletUseCase;
import com.case_wallet.apirest.application.wallet.service.WalletGenerationService;
import com.case_wallet.apirest.common.exception.ResourceNotFoundException;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.domain.user.model.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateWalletUseCaseImpl implements GenerateWalletUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final WalletGenerationService walletGenerationService;
    private final AuthenticationPort authenticationPort;

    @Override
    @Transactional
    public AuthResponseDTO generateWallet(UUID userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        WalletCreationDetails walletDetails = walletGenerationService.generateWallet();

        user.setMantleAddress(walletDetails.getAddress());
        user.setUserState(UserState.WALLET_GENERATED);
        userRepositoryPort.save(user);

        String token = authenticationPort.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .mnemonic(walletDetails.getMnemonic())
                .build();
    }
} 