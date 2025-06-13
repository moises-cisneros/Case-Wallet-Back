package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
