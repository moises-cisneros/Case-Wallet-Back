package com.case_wallet.apirest.application.wallet.port.in;

import com.case_wallet.apirest.application.wallet.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WalletManagementUseCase {
    WalletBalance getBalance(UUID userId);
    DepositResponse requestDeposit(UUID userId, DepositRequest request);
    TransferResponse transferFunds(UUID userId, TransferRequest request);
    Page<TransactionDTO> getTransactions(UUID userId, Pageable pageable);
    
    /**
     * Obtiene o crea una dirección de depósito para criptomonedas
     */
    String getOrCreateDepositAddress(UUID userId, String currency);
    
    /**
     * Procesa una solicitud de retiro de criptomonedas
     */
    CryptoWithdrawResponse requestCryptoWithdrawal(UUID userId, CryptoWithdrawRequest request);
    
    /**
     * Obtiene el historial de retiros de un usuario
     */
    List<CryptoWithdrawResponse> getWithdrawalHistory(UUID userId);
    
    /**
     * Obtiene el estado de un retiro específico
     */
    CryptoWithdrawResponse getWithdrawalStatus(UUID userId, Long withdrawalId);
}
