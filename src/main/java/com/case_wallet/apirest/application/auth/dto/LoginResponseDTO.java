package com.case_wallet.apirest.application.auth.dto;

import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private UserProfileDTO user;
}
