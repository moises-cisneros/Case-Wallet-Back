package com.case_wallet.apirest.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileDTO {
    private UUID id;
    private String email;
    //private String password; // Optional, for password updates
    //private String pinHash; // Optional, for PIN updates
    //private String mantleAddress; // Optional, for updating the Mantle address
} 