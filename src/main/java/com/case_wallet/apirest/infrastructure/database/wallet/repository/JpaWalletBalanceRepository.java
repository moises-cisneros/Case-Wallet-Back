package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaWalletBalanceRepository extends JpaRepository<WalletBalanceEntity, UUID> {
    Optional<WalletBalanceEntity> findByUserId(UUID userId);
}
