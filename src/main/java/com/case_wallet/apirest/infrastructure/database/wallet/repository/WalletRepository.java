package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByUserId(UUID userId);
}
