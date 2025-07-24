package com.case_wallet.apirest.application.wallet.usecases;

import com.case_wallet.apirest.application.wallet.dto.*;
import com.case_wallet.apirest.application.wallet.port.in.WalletManagementUseCase;
import com.case_wallet.apirest.application.wallet.port.out.WalletRepositoryPort;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletBalanceEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.DepositEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransferEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletManagementUseCaseImpl implements WalletManagementUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public WalletBalance getBalance(UUID userId) {
        WalletBalanceEntity balanceEntity = walletRepositoryPort.findBalanceByUserId(userId)
                .orElseGet(() -> walletRepositoryPort.createInitialBalance(userId));

        return WalletBalance.builder()
                .balanceBS(balanceEntity.getBalanceBS())
                .balanceUSDT(balanceEntity.getBalanceUSDT())
                .exchangeRate(balanceEntity.getExchangeRate())
                .build();
    }

    @Override
    @Transactional
    public DepositResponse requestDeposit(UUID userId, DepositRequest request) {
        // Verify user exists
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Create deposit entity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        DepositEntity depositEntity = DepositEntity.builder()
                .user(userEntity)
                .amount(request.getAmount())
                .receiptImageUrl(request.getReceiptImageUrl())
                .status("PENDING")
                .build();

        DepositEntity savedDeposit = walletRepositoryPort.saveDeposit(depositEntity);

        log.info("Deposit request created for user {} with amount {}", userId, request.getAmount());

        return DepositResponse.builder()
                .depositId(savedDeposit.getId())
                .status(savedDeposit.getStatus())
                .build();
    }

    @Override
    @Transactional
    public TransferResponse transferFunds(UUID userId, TransferRequest request) {
        // Verify sender exists
        User sender = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario emisor no encontrado"));

        // Verify recipient exists
        userRepositoryPort.findById(request.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));

        // Verify PIN
        if (!passwordEncoder.matches(request.getPin(), sender.getPinHash())) {
            throw new IllegalArgumentException("PIN incorrecto");
        }

        // Check balance
        WalletBalanceEntity senderBalance = walletRepositoryPort.findBalanceByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Balance del emisor no encontrado"));

        BigDecimal availableBalance = "BS".equals(request.getCurrency()) 
                ? senderBalance.getBalanceBS() 
                : senderBalance.getBalanceUSDT();

        if (availableBalance.compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        // Create transfer entities
        UserEntity senderEntity = new UserEntity();
        senderEntity.setId(userId);
        
        UserEntity recipientEntity = new UserEntity();
        recipientEntity.setId(request.getRecipientId());

        TransferEntity transferEntity = TransferEntity.builder()
                .sender(senderEntity)
                .recipient(recipientEntity)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("PENDING")
                .build();

        TransferEntity savedTransfer = walletRepositoryPort.saveTransfer(transferEntity);

        // Update balances (in a real implementation, this would be more sophisticated)
        if ("BS".equals(request.getCurrency())) {
            senderBalance.setBalanceBS(senderBalance.getBalanceBS().subtract(request.getAmount()));
        } else {
            senderBalance.setBalanceUSDT(senderBalance.getBalanceUSDT().subtract(request.getAmount()));
        }
        walletRepositoryPort.saveBalance(senderBalance);

        // Update recipient balance
        WalletBalanceEntity recipientBalance = walletRepositoryPort.findBalanceByUserId(request.getRecipientId())
                .orElseGet(() -> walletRepositoryPort.createInitialBalance(request.getRecipientId()));

        if ("BS".equals(request.getCurrency())) {
            recipientBalance.setBalanceBS(recipientBalance.getBalanceBS().add(request.getAmount()));
        } else {
            recipientBalance.setBalanceUSDT(recipientBalance.getBalanceUSDT().add(request.getAmount()));
        }
        walletRepositoryPort.saveBalance(recipientBalance);

        // Update transfer status
        savedTransfer.setStatus("COMPLETED");
        walletRepositoryPort.saveTransfer(savedTransfer);

        log.info("Transfer completed from user {} to user {} for amount {} {}", 
                userId, request.getRecipientId(), request.getAmount(), request.getCurrency());

        return TransferResponse.builder()
                .transactionId(savedTransfer.getId())
                .status(savedTransfer.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactions(UUID userId, Pageable pageable) {
        return walletRepositoryPort.findTransactionsByUserId(userId, pageable);
    }
}
