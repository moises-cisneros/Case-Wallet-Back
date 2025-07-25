package com.case_wallet.apirest.application.wallet.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para solicitud de retiro de criptomonedas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoWithdrawRequest {
    
    @NotBlank(message = "La moneda es requerida")
    @Pattern(regexp = "^(USDT|ETH|BTC)$", message = "Moneda no soportada")
    private String currency;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto mínimo es 0.01")
    @DecimalMax(value = "100000", message = "El monto máximo es 100,000")
    private BigDecimal amount;
    
    @NotBlank(message = "La dirección de destino es requerida")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Dirección de wallet inválida")
    private String destinationAddress;
    
    @NotBlank(message = "El PIN es requerido")
    @Size(min = 4, max = 6, message = "El PIN debe tener entre 4 y 6 dígitos")
    private String pin;
}
