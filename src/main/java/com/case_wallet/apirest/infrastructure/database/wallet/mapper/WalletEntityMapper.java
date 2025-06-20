package com.case_wallet.apirest.infrastructure.database.wallet.mapper;

import com.case_wallet.apirest.domain.wallet.model.Wallet;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletEntityMapper {
    public WalletEntity toEntity(Wallet wallet) {
        if (wallet == null) return null;

        WalletEntity entity = new WalletEntity();
        entity.setId(wallet.getId());
        entity.setUser(new UserEntity(wallet.getUserId()));
        entity.setBalanceLocal(wallet.getBalanceLocal());
        entity.setBalanceCrypto(wallet.getBalanceCrypto());
        entity.setUpdatedAt(wallet.getUpdatedAt());

        return entity;
    }
}
