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
                .balanceLocal(entity.getBalanceLocal())
                .balanceCrypto(entity.getBalanceCrypto())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public WalletBalanceDTO toBalanceDTO(Wallet wallet) {
        if (wallet == null) return null;
        NumberFormat localFormat = NumberFormat.getCurrencyInstance(new Locale("es", "VE"));
        NumberFormat cryptoFormat = NumberFormat.getCurrencyInstance(Locale.US);

        return WalletBalanceDTO.builder()
                .balanceLocal(wallet.getBalanceLocal())
                .balanceCrypto(wallet.getBalanceCrypto())
                .formattedBalanceLocal(localFormat.format(wallet.getBalanceLocal()))
                .formattedBalanceCrypto(cryptoFormat.format(wallet.getBalanceCrypto()))
                .cryptoWalletAddress(wallet.getCryptoWalletAddress())
                .build();
    }
}
