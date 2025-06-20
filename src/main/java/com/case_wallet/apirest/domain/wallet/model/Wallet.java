package com.case_wallet.apirest.domain.wallet.model;

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
public class Wallet {
    private UUID id;
    private UUID userId;
    private BigDecimal balanceLocal;
    private BigDecimal balanceCrypto;
    private String cryptoWalletAddress;
    private LocalDateTime updatedAt;
}
