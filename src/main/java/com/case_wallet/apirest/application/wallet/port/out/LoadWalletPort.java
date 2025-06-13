package com.case_wallet.apirest.application.wallet.port.out;

import com.case_wallet.apirest.domain.wallet.model.Wallet;
import com.case_wallet.apirest.domain.wallet.model.Transaction;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadWalletPort {
    Optional<Wallet> loadWalletByUserId(UUID userId);
    Wallet saveWallet(Wallet wallet);
    Page<Transaction> loadTransactionsByUserId(UUID userId, Pageable pageable);
    Transaction saveTransaction(Transaction transaction);
    Optional<Transaction> loadTransactionById(UUID transactionId);
}
