package com.case_wallet.apirest.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSmsRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{8}$", message = "Phone number must be 8 digits")
    private String phoneNumber;
}
