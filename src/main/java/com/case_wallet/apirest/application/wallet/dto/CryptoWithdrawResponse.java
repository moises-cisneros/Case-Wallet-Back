package com.case_wallet.apirest.application.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de retiro de criptomonedas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoWithdrawResponse {
    
    private Long withdrawalId;
    private String currency;
    private BigDecimal amount;
    private String destinationAddress;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String transactionHash; // Hash cuando esté disponible
    private LocalDateTime createdAt;
    private LocalDateTime estimatedCompletion; // Estimación de cuándo se completará
    private String message; // Mensaje descriptivo del estado
}
