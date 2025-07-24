package com.case_wallet.apirest.application.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalance {
    private BigDecimal balanceBS;
    private BigDecimal balanceUSDT;
    private BigDecimal exchangeRate;
}
