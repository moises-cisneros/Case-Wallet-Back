package com.case_wallet.apirest.application.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotNull(message = "El ID del destinatario es requerido")
    private UUID recipientId;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;
    
    @NotBlank(message = "La moneda es requerida")
    private String currency;
    
    @NotBlank(message = "El PIN es requerido")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener 4 dígitos")
    private String pin;
}
