package com.case_wallet.apirest.application.wallet.mapper;

import com.case_wallet.apirest.application.wallet.dto.WalletBalanceDTO;
import com.case_wallet.apirest.domain.wallet.model.Wallet;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletEntity;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
public class WalletMapper {

    public Wallet toModel(WalletEntity entity) {
        if (entity == null) return null;
        return Wallet.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .balanceBs(entity.getBalanceBs())
                .balanceUsdt(entity.getBalanceUsdt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public WalletBalanceDTO toBalanceDTO(Wallet wallet) {
        if (wallet == null) return null;
        NumberFormat bsFormat = NumberFormat.getCurrencyInstance(new Locale("es", "VE"));
        NumberFormat usdtFormat = NumberFormat.getCurrencyInstance(Locale.US);

        return WalletBalanceDTO.builder()
                .balanceBs(wallet.getBalanceBs())
                .balanceUsdt(wallet.getBalanceUsdt())
                .formattedBalanceBs(bsFormat.format(wallet.getBalanceBs()))
                .formattedBalanceUsdt(usdtFormat.format(wallet.getBalanceUsdt()))
                .build();
    }
}
