package com.case_wallet.apirest.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRegistrationRequest {
    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El número de teléfono debe tener 8 dígitos")
    private String phoneNumber;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    @NotBlank(message = "El PIN es requerido")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener 4 dígitos")
    private String pin;
}
