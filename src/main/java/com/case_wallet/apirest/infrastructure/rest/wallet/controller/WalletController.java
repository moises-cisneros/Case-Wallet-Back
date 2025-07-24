package com.case_wallet.apirest.infrastructure.rest.wallet.controller;

import com.case_wallet.apirest.application.wallet.dto.*;
import com.case_wallet.apirest.application.wallet.port.in.WalletManagementUseCase;
import com.case_wallet.apirest.common.dto.ApiResponse;
import com.case_wallet.apirest.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletManagementUseCase walletManagementUseCase;
    private final SecurityService securityService;

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WalletBalance>> getBalance() {
        try {
            UUID userId = securityService.getCurrentUserId();
            WalletBalance balance = walletManagementUseCase.getBalance(userId);
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (Exception e) {
            log.error("Error getting balance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error obteniendo balance"));
        }
    }

    @PostMapping("/deposit/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DepositResponse>> requestDeposit(@Valid @RequestBody DepositRequest request) {
        try {
            UUID userId = securityService.getCurrentUserId();
            DepositResponse response = walletManagementUseCase.requestDeposit(userId, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Depósito solicitado exitosamente"));
        } catch (Exception e) {
            log.error("Error requesting deposit: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error solicitando depósito"));
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            UUID userId = securityService.getCurrentUserId();
            TransferResponse response = walletManagementUseCase.transferFunds(userId, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Transferencia realizada exitosamente"));
        } catch (Exception e) {
            log.error("Error processing transfer: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error procesando transferencia"));
        }
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            UUID userId = securityService.getCurrentUserId();
            Pageable pageable = PageRequest.of(page, limit);
            Page<TransactionDTO> transactions = walletManagementUseCase.getTransactions(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            log.error("Error getting transactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "Error obteniendo transacciones"));
        }
    }
}
