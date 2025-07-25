package com.case_wallet.apirest.infrastructure.rest.wallet;

import com.case_wallet.apirest.application.wallet.dto.CryptoAddressRequest;
import com.case_wallet.apirest.application.wallet.dto.CryptoAddressResponse;
import com.case_wallet.apirest.application.wallet.dto.CryptoWithdrawRequest;
import com.case_wallet.apirest.application.wallet.dto.CryptoWithdrawResponse;
import com.case_wallet.apirest.application.wallet.port.in.WalletManagementUseCase;
import com.case_wallet.apirest.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para operaciones de criptomonedas
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/crypto/wallet")
@RequiredArgsConstructor
public class CryptoWalletController {

    private final WalletManagementUseCase walletManagementUseCase;

    /**
     * Obtiene o crea una dirección de depósito para el usuario autenticado
     */
    @GetMapping("/address")
    public ResponseEntity<ApiResponse<CryptoAddressResponse>> getDepositAddress(
            @RequestParam String currency) {
        
        try {
            UUID currentUserId = getCurrentUserId();
            log.info("Usuario {} solicitando dirección de depósito para {}", currentUserId, currency);
            
            // Validar moneda
            if (!"USDT".equals(currency)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<CryptoAddressResponse>builder()
                                .success(false)
                                .message("Moneda no soportada: " + currency)
                                .build());
            }
            
            String address = walletManagementUseCase.getOrCreateDepositAddress(currentUserId, currency);
            
            CryptoAddressResponse response = CryptoAddressResponse.builder()
                    .currency(currency)
                    .address(address)
                    .network("Mantle Sepolia")
                    .status("ACTIVE")
                    .build();
            
            return ResponseEntity.ok(
                    ApiResponse.<CryptoAddressResponse>builder()
                            .success(true)
                            .message("Dirección obtenida exitosamente")
                            .data(response)
                            .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al obtener dirección: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CryptoAddressResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al obtener dirección de depósito: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CryptoAddressResponse>builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Endpoint alternativo usando POST para mayor flexibilidad
     */
    @PostMapping("/address")
    public ResponseEntity<ApiResponse<CryptoAddressResponse>> createDepositAddress(
            @Valid @RequestBody CryptoAddressRequest request) {
        
        try {
            UUID currentUserId = getCurrentUserId();
            log.info("Usuario {} creando dirección de depósito para {}", currentUserId, request.getCurrency());
            
            String address = walletManagementUseCase.getOrCreateDepositAddress(currentUserId, request.getCurrency());
            
            CryptoAddressResponse response = CryptoAddressResponse.builder()
                    .currency(request.getCurrency())
                    .address(address)
                    .network("Mantle Sepolia")
                    .status("ACTIVE")
                    .build();
            
            return ResponseEntity.ok(
                    ApiResponse.<CryptoAddressResponse>builder()
                            .success(true)
                            .message("Dirección creada exitosamente")
                            .data(response)
                            .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear dirección: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CryptoAddressResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al crear dirección de depósito: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CryptoAddressResponse>builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtiene el ID del usuario actual desde el contexto de seguridad
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        
        // Asumiendo que el principal contiene el ID del usuario
        // Ajustar según la implementación de seguridad actual
        String userIdStr = authentication.getName();
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("ID de usuario inválido en el token: " + userIdStr);
        }
    }

    /**
     * Procesa una solicitud de retiro de criptomonedas
     */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<CryptoWithdrawResponse>> withdrawCrypto(
            @Valid @RequestBody CryptoWithdrawRequest request) {
        
        try {
            UUID currentUserId = getCurrentUserId();
            log.info("Usuario {} solicitando retiro de {} {}", 
                    currentUserId, request.getAmount(), request.getCurrency());
            
            CryptoWithdrawResponse response = walletManagementUseCase
                    .requestCryptoWithdrawal(currentUserId, request);
            
            return ResponseEntity.ok(
                    ApiResponse.<CryptoWithdrawResponse>builder()
                            .success(true)
                            .message("Retiro procesado exitosamente")
                            .data(response)
                            .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en retiro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CryptoWithdrawResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno en retiro: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CryptoWithdrawResponse>builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtiene el historial de retiros del usuario
     */
    @GetMapping("/withdrawals")
    public ResponseEntity<ApiResponse<List<CryptoWithdrawResponse>>> getWithdrawalHistory() {
        
        try {
            UUID currentUserId = getCurrentUserId();
            log.debug("Usuario {} consultando historial de retiros", currentUserId);
            
            List<CryptoWithdrawResponse> withdrawals = walletManagementUseCase
                    .getWithdrawalHistory(currentUserId);
            
            return ResponseEntity.ok(
                    ApiResponse.<List<CryptoWithdrawResponse>>builder()
                            .success(true)
                            .message("Historial obtenido exitosamente")
                            .data(withdrawals)
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error al obtener historial de retiros: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<CryptoWithdrawResponse>>builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtiene el estado de un retiro específico
     */
    @GetMapping("/withdrawals/{withdrawalId}")
    public ResponseEntity<ApiResponse<CryptoWithdrawResponse>> getWithdrawalStatus(
            @PathVariable Long withdrawalId) {
        
        try {
            UUID currentUserId = getCurrentUserId();
            log.debug("Usuario {} consultando estado del retiro {}", currentUserId, withdrawalId);
            
            CryptoWithdrawResponse withdrawal = walletManagementUseCase
                    .getWithdrawalStatus(currentUserId, withdrawalId);
            
            return ResponseEntity.ok(
                    ApiResponse.<CryptoWithdrawResponse>builder()
                            .success(true)
                            .message("Estado obtenido exitosamente")
                            .data(withdrawal)
                            .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Retiro no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al obtener estado de retiro: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CryptoWithdrawResponse>builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }
}
