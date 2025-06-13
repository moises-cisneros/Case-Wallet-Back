package com.case_wallet.apirest.application.user.dto;

import com.case_wallet.apirest.domain.user.model.KYCStatus;
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
public class UserProfileDTO {
    private UUID id;
    private String phoneNumber;
    private Role role;
    private KYCStatus kycStatus;
    private String mantleAddress;
}
