package com.case_wallet.apirest.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneLoginRequest {
    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El número de teléfono debe tener 8 dígitos")
    private String phoneNumber;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
