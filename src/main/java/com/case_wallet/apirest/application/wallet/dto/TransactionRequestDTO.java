package com.case_wallet.apirest.application.wallet.dto;

import com.case_wallet.apirest.domain.wallet.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    @NotNull
    private TransactionType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    private String currency;

    @Pattern(regexp = "^(0x)?[0-9a-fA-F]{40}$", message = "Dirección de blockchain inválida")
    private String blockchainAddress;

    private String transactionHash;

    private Map<String, Object> metadata;
}
