package com.case_wallet.apirest.application.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar una dirección de depósito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAddressRequest {
    
    @NotBlank(message = "La moneda es requerida")
    @Pattern(regexp = "^(USDT|ETH|BTC)$", message = "Moneda no soportada. Valores permitidos: USDT, ETH, BTC")
    private String currency;
}
