package com.case_wallet.apirest.application.wallet.port.out;

import com.case_wallet.apirest.application.wallet.dto.TransactionDTO;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletBalanceEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.DepositEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {
    Optional<WalletBalanceEntity> findBalanceByUserId(UUID userId);
    WalletBalanceEntity saveBalance(WalletBalanceEntity balance);
    WalletBalanceEntity createInitialBalance(UUID userId);
    
    DepositEntity saveDeposit(DepositEntity deposit);
    
    TransferEntity saveTransfer(TransferEntity transfer);
    
    Page<TransactionDTO> findTransactionsByUserId(UUID userId, Pageable pageable);
}
