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
public class RegisterRequestDTO {
    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El número de teléfono debe tener exactamente 8 dígitos")
    private String phoneNumber;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El PIN es requerido")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener exactamente 4 dígitos")
    private String pin;
}
