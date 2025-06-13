package com.case_wallet.apirest.application.auth.usecases;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.RegisterRequestDTO;
import com.case_wallet.apirest.application.auth.port.in.RegisterUserUseCase;
import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.application.user.mapper.UserMapper;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.Role;
import com.case_wallet.apirest.domain.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final AuthenticationPort authenticationPort;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepositoryPort.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado");
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .pinHash(passwordEncoder.encode(request.getPin()))
                .role(Role.USER)
                .build();

        User savedUser = userRepositoryPort.save(user);
        String token = authenticationPort.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO registerAdmin(RegisterRequestDTO request) {
        if (userRepositoryPort.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado");
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .pinHash(passwordEncoder.encode(request.getPin()))
                .role(Role.ADMIN)
                .build();

        User savedUser = userRepositoryPort.save(user);
        String token = authenticationPort.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole())
                .build();
    }
}
