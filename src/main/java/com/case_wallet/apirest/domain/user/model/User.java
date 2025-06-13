package com.case_wallet.apirest.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String phoneNumber;
    private String password;
    private String pinHash;
    private Role role;
    private KYCStatus kycStatus;
    private String mantleAddress;
}
