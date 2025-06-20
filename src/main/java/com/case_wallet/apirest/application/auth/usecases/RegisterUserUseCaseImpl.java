package com.case_wallet.apirest.application.auth.usecases;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.RegisterRequestDTO;
import com.case_wallet.apirest.application.auth.googleservice.GoogleTokenVerifierService;
import com.case_wallet.apirest.application.auth.port.in.RegisterUserUseCase;
import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.application.user.mapper.UserMapper;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.KYCStatus;
import com.case_wallet.apirest.domain.user.model.Role;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.domain.user.model.UserState;
import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;
import com.case_wallet.apirest.infrastructure.database.user.repository.JpaRoleRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final AuthenticationPort authenticationPort;
    private final JpaRoleRepository jpaRoleRepository;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        GoogleIdToken.Payload payload;
        try {
            payload = googleTokenVerifierService.verifyToken(request.getGoogleIdToken());
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Invalid Google ID Token", e);
        }

        if (payload == null) {
            throw new IllegalArgumentException("Invalid Google ID Token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String googleId = payload.getSubject();

        if (userRepositoryPort.existsByGoogleId(googleId)) {
            throw new IllegalArgumentException("El usuario ya está registrado con este Google ID");
        }

        RoleEntity userRole = jpaRoleRepository.findByName(Role.USER.name())
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .email(email)
                .name(name)
                .googleId(googleId)
                .roles(userMapper.toDomainRoles(roles))
                .kycStatus(KYCStatus.NONE)
                .userState(UserState.PENDING_WALLET)
                .build();

        User savedUser = userRepositoryPort.save(user);
        String token = authenticationPort.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO registerAdmin(RegisterRequestDTO request) {
        GoogleIdToken.Payload payload;
        try {
            payload = googleTokenVerifierService.verifyToken(request.getGoogleIdToken());
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Invalid Google ID Token", e);
        }

        if (payload == null) {
            throw new IllegalArgumentException("Invalid Google ID Token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String googleId = payload.getSubject();

        if (userRepositoryPort.existsByGoogleId(googleId)) {
            throw new IllegalArgumentException("El usuario ya está registrado con este Google ID");
        }

        RoleEntity adminRole = jpaRoleRepository.findByName(Role.ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("Role ADMIN not found"));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(adminRole);

        User user = User.builder()
                .email(email)
                .name(name)
                .googleId(googleId)
                .roles(userMapper.toDomainRoles(roles))
                .kycStatus(KYCStatus.NONE)
                .userState(UserState.PENDING_WALLET)
                .build();

        User savedUser = userRepositoryPort.save(user);
        String token = authenticationPort.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }
}
