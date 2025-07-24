package com.case_wallet.apirest.infrastructure.rest.auth.controller;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.LoginRequestDTO;
import com.case_wallet.apirest.application.auth.dto.RegisterRequestDTO;
import com.case_wallet.apirest.application.auth.port.in.AuthenticateUserUseCase;
import com.case_wallet.apirest.application.auth.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authenticateUserUseCase.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = registerUserUseCase.register(request);
        return ResponseEntity.ok(response);
    }
}
