package com.case_wallet.apirest.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String name;
    private String googleId;
    private Set<Role> roles = new HashSet<>();
    private KYCStatus kycStatus;
    private UserState userState;
    private String mantleAddress;
    private Boolean enabled = true;
}
