package com.case_wallet.apirest.application.auth.dto;

import com.case_wallet.apirest.domain.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UUID userId;
    private String email;
    private String mnemonic;
    private Role role;
}
