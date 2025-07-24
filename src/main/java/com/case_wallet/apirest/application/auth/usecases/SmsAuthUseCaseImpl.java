package com.case_wallet.apirest.application.auth.usecases;

import com.case_wallet.apirest.application.auth.dto.*;
import com.case_wallet.apirest.application.auth.port.in.SmsAuthUseCase;
import com.case_wallet.apirest.application.auth.port.out.SmsService;
import com.case_wallet.apirest.application.auth.port.out.SmsVerificationPort;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.KYCStatus;
import com.case_wallet.apirest.domain.user.model.Role;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.domain.user.model.UserState;
import com.case_wallet.apirest.infrastructure.database.auth.entity.SmsVerificationEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsAuthUseCaseImpl implements SmsAuthUseCase {

    private final SmsVerificationPort smsVerificationPort;
    private final SmsService smsService;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void requestSms(RequestSmsRequest request) {
        String phoneNumber = request.getPhoneNumber();
        
        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Save OTP to database
        SmsVerificationEntity smsVerification = SmsVerificationEntity.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5 minutes expiration
                .build();
        
        smsVerificationPort.save(smsVerification);
        
        // Send SMS
        String message = String.format("Tu código de verificación es: %s. Válido por 5 minutos.", otpCode);
        smsService.sendSms(phoneNumber, message);
        
        log.info("SMS sent to phone number: {}", phoneNumber);
    }

    @Override
    @Transactional
    public void verifySms(VerifySmsRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String otpCode = request.getOtpCode();
        
        SmsVerificationEntity verification = smsVerificationPort
                .findByPhoneNumberAndOtpCode(phoneNumber, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("Código OTP inválido"));
        
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El código OTP ha expirado");
        }
        
        if (verification.getIsVerified()) {
            throw new IllegalArgumentException("El código OTP ya ha sido utilizado");
        }
        
        // Mark as verified
        verification.setIsVerified(true);
        smsVerificationPort.save(verification);
        
        log.info("SMS verified for phone number: {}", phoneNumber);
    }

    @Override
    @Transactional
    public AuthResponseDTO completeRegistration(CompleteRegistrationRequest request) {
        String phoneNumber = request.getPhoneNumber();
        
        // Verify that SMS was verified
        SmsVerificationEntity verification = smsVerificationPort
                .findLatestVerifiedSmsByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Debe verificar el SMS antes de completar el registro"));
        
        // Check if user already exists
        if (userRepositoryPort.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Ya existe un usuario con este número de teléfono");
        }
        
        // Create new user
        User user = User.builder()
                .phoneNumber(phoneNumber)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .pinHash(passwordEncoder.encode(request.getPin()))
                .roles(Set.of(Role.USER))
                .kycStatus(KYCStatus.PENDING)
                .userState(UserState.ACTIVE)
                .enabled(true)
                .build();
        
        User savedUser = userRepositoryPort.save(user);
        
        // Generate JWT token using the saved user entity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(savedUser.getId());
        userEntity.setPhoneNumber(savedUser.getPhoneNumber());
        userEntity.setPasswordHash(savedUser.getPasswordHash());
        String token = jwtService.getToken(userEntity);
        
        log.info("User registered successfully with phone number: {}", phoneNumber);
        
        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getPhoneNumber()) // Using phone as email for compatibility
                .role(Role.USER)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO loginWithPhone(PhoneLoginRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String password = request.getPassword();
        
        User user = userRepositoryPort.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        
        if (!user.getEnabled()) {
            throw new IllegalArgumentException("Usuario deshabilitado");
        }
        
        // Generate JWT token using the user entity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setPhoneNumber(user.getPhoneNumber());
        userEntity.setPasswordHash(user.getPasswordHash());
        String token = jwtService.getToken(userEntity);
        
        log.info("User logged in successfully with phone number: {}", phoneNumber);
        
        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getPhoneNumber())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        // For now, we'll implement a simple refresh that generates a new token
        // In a production environment, you'd want to implement proper refresh token logic
        String oldToken = request.getRefreshToken();
        
        try {
            String username = jwtService.extractUsername(oldToken);
            if (username != null && !isTokenExpired(oldToken)) {
                // Create a simple UserEntity for token generation
                UserEntity userEntity = new UserEntity();
                userEntity.setPhoneNumber(username);
                String newToken = jwtService.getToken(userEntity);
                
                return RefreshTokenResponse.builder()
                        .accessToken(newToken)
                        .refreshToken(newToken) // In production, generate a separate refresh token
                        .build();
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
        }
        
        throw new IllegalArgumentException("Token de actualización inválido");
    }
    
    // Helper method to check if token is expired
    private boolean isTokenExpired(String token) {
        try {
            return jwtService.extractClaim(token, claims -> claims.getExpiration())
                    .before(new java.util.Date());
        } catch (Exception e) {
            return true;
        }
    }
}
