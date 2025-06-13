package com.case_wallet.apirest.domain.auth.model;

import com.case_wallet.apirest.domain.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String phoneNumber;
    private Role role;
}
