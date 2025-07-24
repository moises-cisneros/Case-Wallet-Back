package com.case_wallet.apirest.infrastructure.database.wallet.adapter;

import com.case_wallet.apirest.application.wallet.dto.TransactionDTO;
import com.case_wallet.apirest.application.wallet.port.out.WalletRepositoryPort;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletBalanceEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.DepositEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransferEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.JpaWalletBalanceRepository;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.JpaDepositRepository;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.JpaTransferRepository;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.TransactionRepository;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final JpaWalletBalanceRepository walletBalanceRepository;
    private final JpaDepositRepository depositRepository;
    private final JpaTransferRepository transferRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Optional<WalletBalanceEntity> findBalanceByUserId(UUID userId) {
        return walletBalanceRepository.findByUserId(userId);
    }

    @Override
    public WalletBalanceEntity saveBalance(WalletBalanceEntity balance) {
        return walletBalanceRepository.save(balance);
    }

    @Override
    public WalletBalanceEntity createInitialBalance(UUID userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        WalletBalanceEntity balance = WalletBalanceEntity.builder()
                .user(userEntity)
                .balanceBS(BigDecimal.ZERO)
                .balanceUSDT(BigDecimal.ZERO)
                .exchangeRate(BigDecimal.valueOf(36.5)) // Default exchange rate
                .build();

        return walletBalanceRepository.save(balance);
    }

    @Override
    public DepositEntity saveDeposit(DepositEntity deposit) {
        return depositRepository.save(deposit);
    }

    @Override
    public TransferEntity saveTransfer(TransferEntity transfer) {
        return transferRepository.save(transfer);
    }

    @Override
    public Page<TransactionDTO> findTransactionsByUserId(UUID userId, Pageable pageable) {
        // For now, we'll create a simple implementation
        // In a real implementation, you would query from transaction tables
        List<TransactionDTO> transactions = new ArrayList<>();
        
        // This is a simplified implementation - you'd want to create proper queries
        // to combine deposits, transfers, and other transaction types
        
        return new PageImpl<>(transactions, pageable, 0);
    }
}
