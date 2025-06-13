package com.case_wallet.apirest.application.wallet.usecases;

import com.case_wallet.apirest.application.wallet.dto.TransactionRequestDTO;
import com.case_wallet.apirest.application.wallet.dto.WalletBalanceDTO;
import com.case_wallet.apirest.application.wallet.mapper.WalletMapper;
import com.case_wallet.apirest.application.wallet.port.in.WalletUseCase;
import com.case_wallet.apirest.application.wallet.port.out.LoadWalletPort;
import com.case_wallet.apirest.domain.wallet.model.Transaction;
import com.case_wallet.apirest.domain.wallet.model.TransactionStatus;
import com.case_wallet.apirest.domain.wallet.model.Wallet;
import com.case_wallet.apirest.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletUseCase {
    private final LoadWalletPort loadWalletPort;
    private final WalletMapper walletMapper;

    @Override
    public WalletBalanceDTO getBalance(UUID userId) {
        return loadWalletPort.loadWalletByUserId(userId)
                .map(walletMapper::toBalanceDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    @Override
    public Page<Transaction> getTransactions(UUID userId, Pageable pageable) {
        return loadWalletPort.loadTransactionsByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Transaction createTransaction(UUID userId, TransactionRequestDTO request) {
        Wallet wallet = loadWalletPort.loadWalletByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(TransactionStatus.PENDING)
                .metadata(request.getMetadata())
                .build();

        return loadWalletPort.saveTransaction(transaction);
    }

    @Override
    @Transactional
    public Transaction confirmTransaction(UUID userId, UUID transactionId) {
        Transaction transaction = loadWalletPort.loadTransactionById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (!transaction.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Transaction does not belong to user");
        }

        Wallet wallet = loadWalletPort.loadWalletByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        // Actualizar el saldo según el tipo de transacción y la moneda
        updateWalletBalance(wallet, transaction);
        loadWalletPort.saveWallet(wallet);

        transaction.setStatus(TransactionStatus.COMPLETED);
        return loadWalletPort.saveTransaction(transaction);
    }

    private void updateWalletBalance(Wallet wallet, Transaction transaction) {
        switch (transaction.getCurrency().toUpperCase()) {
            case "BS":
                updateBsBalance(wallet, transaction);
                break;
            case "USDT":
                updateUsdtBalance(wallet, transaction);
                break;
            default:
                throw new IllegalArgumentException("Unsupported currency: " + transaction.getCurrency());
        }
    }

    private void updateBsBalance(Wallet wallet, Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
                wallet.setBalanceBs(wallet.getBalanceBs().add(transaction.getAmount()));
                break;
            case WITHDRAWAL:
                if (wallet.getBalanceBs().compareTo(transaction.getAmount()) < 0) {
                    throw new IllegalStateException("Insufficient BS balance");
                }
                wallet.setBalanceBs(wallet.getBalanceBs().subtract(transaction.getAmount()));
                break;
            // Implementar otros tipos de transacciones según sea necesario
        }
    }

    private void updateUsdtBalance(Wallet wallet, Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
                wallet.setBalanceUsdt(wallet.getBalanceUsdt().add(transaction.getAmount()));
                break;
            case WITHDRAWAL:
                if (wallet.getBalanceUsdt().compareTo(transaction.getAmount()) < 0) {
                    throw new IllegalStateException("Insufficient USDT balance");
                }
                wallet.setBalanceUsdt(wallet.getBalanceUsdt().subtract(transaction.getAmount()));
                break;
            // Implementar otros tipos de transacciones según sea necesario
        }
    }
}
