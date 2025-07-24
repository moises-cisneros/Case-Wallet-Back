package com.case_wallet.apirest.application.wallet.port.in;

import com.case_wallet.apirest.application.wallet.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletManagementUseCase {
    WalletBalance getBalance(UUID userId);
    DepositResponse requestDeposit(UUID userId, DepositRequest request);
    TransferResponse transferFunds(UUID userId, TransferRequest request);
    Page<TransactionDTO> getTransactions(UUID userId, Pageable pageable);
}
