package com.case_wallet.apirest.application.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private UUID id;
    private String type;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private String description;
}
