package com.case_wallet.apirest.infrastructure.rest.wallet;

import com.case_wallet.apirest.application.wallet.dto.TransactionRequestDTO;
import com.case_wallet.apirest.application.wallet.dto.WalletBalanceDTO;
import com.case_wallet.apirest.application.wallet.port.in.WalletUseCase;
import com.case_wallet.apirest.domain.wallet.model.Transaction;
//import com.case_wallet.apirest.infrastructure.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletUseCase walletUseCase;
    //private final SecurityService securityService;

    /*
    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletBalanceDTO> getBalance() {
        UUID userId = securityService.getCurrentUserId();
        return ResponseEntity.ok(walletUseCase.getBalance(userId));
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Transaction>> getTransactions(Pageable pageable) {
        UUID userId = securityService.getCurrentUserId();
        return ResponseEntity.ok(walletUseCase.getTransactions(userId, pageable));
    }

    @PostMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody TransactionRequestDTO request) {
        UUID userId = securityService.getCurrentUserId();
        return ResponseEntity.ok(walletUseCase.createTransaction(userId, request));
    }

    @PutMapping("/transactions/{transactionId}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> confirmTransaction(
            @PathVariable UUID transactionId) {
        UUID userId = securityService.getCurrentUserId();
        return ResponseEntity.ok(walletUseCase.confirmTransaction(userId, transactionId));
    }*/
}
