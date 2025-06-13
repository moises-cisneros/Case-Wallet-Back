package com.case_wallet.apirest.application.auth.usecases;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.LoginRequestDTO;
import com.case_wallet.apirest.application.auth.port.in.AuthenticateUserUseCase;
import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final AuthenticationPort authenticationPort;

    @Override
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        User user = userRepositoryPort.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = authenticationPort.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}
