package com.case_wallet.apirest.infrastructure.rest.auth.controller;

import com.case_wallet.apirest.application.auth.dto.*;
import com.case_wallet.apirest.application.auth.port.in.SmsAuthUseCase;
import com.case_wallet.apirest.common.dto.ApiResponse;
import com.case_wallet.apirest.domain.auth.model.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class SmsAuthController {

    private final SmsAuthUseCase smsAuthUseCase;

    @PostMapping("/register/request-sms")
    public ResponseEntity<ApiResponse<Object>> requestSms(@Valid @RequestBody RequestSmsRequest request) {
        try {
            smsAuthUseCase.requestSms(request);
            return ResponseEntity.ok(ApiResponse.success(null, "SMS enviado exitosamente"));
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error enviando SMS"));
        }
    }

    @PostMapping("/register/verify-sms")
    public ResponseEntity<ApiResponse<Object>> verifySms(@Valid @RequestBody VerifySmsRequest request) {
        try {
            smsAuthUseCase.verifySms(request);
            return ResponseEntity.ok(ApiResponse.success(null, "SMS verificado exitosamente"));
        } catch (Exception e) {
            log.error("Error verifying SMS: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error verificando SMS"));
        }
    }

    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponse<AuthResponse>> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest request) {
        try {
            AuthResponseDTO authResponseDTO = smsAuthUseCase.completeRegistration(request);
            
            // Convert DTO to domain model for API response
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(authResponseDTO.getUserId())
                    .phoneNumber(authResponseDTO.getEmail()) // Using email field as phone
                    .status("ACTIVE")
                    .kycLevel("PENDING")
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            
            AuthResponse authResponse = AuthResponse.builder()
                    .token(authResponseDTO.getToken())
                    .user(userInfo)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Registro completado exitosamente"));
        } catch (Exception e) {
            log.error("Error completing registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error completando registro"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody PhoneLoginRequest request) {
        try {
            AuthResponseDTO authResponseDTO = smsAuthUseCase.loginWithPhone(request);
            
            // Convert DTO to domain model for API response
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(authResponseDTO.getUserId())
                    .phoneNumber(authResponseDTO.getEmail()) // Using email field as phone
                    .status("ACTIVE")
                    .kycLevel("PENDING")
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            
            AuthResponse authResponse = AuthResponse.builder()
                    .token(authResponseDTO.getToken())
                    .user(userInfo)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login exitoso"));
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error en el login"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshTokenResponse response = smsAuthUseCase.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
