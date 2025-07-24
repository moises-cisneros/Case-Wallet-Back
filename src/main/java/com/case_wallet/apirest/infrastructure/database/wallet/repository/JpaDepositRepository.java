package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.infrastructure.database.wallet.entity.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaDepositRepository extends JpaRepository<DepositEntity, UUID> {
}
