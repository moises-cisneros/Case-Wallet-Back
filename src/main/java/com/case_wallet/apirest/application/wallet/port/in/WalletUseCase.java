package com.case_wallet.apirest.application.wallet.port.in;

import com.case_wallet.apirest.application.wallet.dto.TransactionRequestDTO;
import com.case_wallet.apirest.application.wallet.dto.WalletBalanceDTO;
import com.case_wallet.apirest.domain.wallet.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletUseCase {
    WalletBalanceDTO getBalance(UUID userId);
    Page<Transaction> getTransactions(UUID userId, Pageable pageable);
    Transaction createTransaction(UUID userId, TransactionRequestDTO request);
    Transaction confirmTransaction(UUID userId, UUID transactionId);
}
