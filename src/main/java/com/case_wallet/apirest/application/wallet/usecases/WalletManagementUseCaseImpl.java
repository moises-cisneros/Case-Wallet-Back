package com.case_wallet.apirest.application.wallet.usecases;

import com.case_wallet.apirest.application.wallet.dto.*;
import com.case_wallet.apirest.application.wallet.port.in.WalletManagementUseCase;
import com.case_wallet.apirest.application.wallet.port.out.WalletRepositoryPort;
import com.case_wallet.apirest.application.wallet.service.BlockchainService;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.domain.wallet.model.CryptoWithdrawal;
import com.case_wallet.apirest.domain.wallet.model.UserCryptoAddress;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.WalletBalanceEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.DepositEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransferEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.CryptoWithdrawalRepository;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.UserCryptoAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletManagementUseCaseImpl implements WalletManagementUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final BlockchainService blockchainService;
    private final UserCryptoAddressRepository addressRepository;
    private final CryptoWithdrawalRepository withdrawalRepository;

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

    @Override
    @Transactional
    public String getOrCreateDepositAddress(UUID userId, String currency) {
        log.info("Solicitando dirección de depósito para usuario {} y moneda {}", userId, currency);
        
        // Validar que el usuario existe
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Validar moneda soportada
        if (!"USDT".equals(currency)) {
            throw new IllegalArgumentException("Moneda no soportada: " + currency);
        }
        
        // Buscar dirección existente en base de datos
        Long userInternalId = getUserInternalId(userId);
        Optional<UserCryptoAddress> existingAddress = 
                addressRepository.findByUserIdAndCurrency(userInternalId, currency);
        
        if (existingAddress.isPresent()) {
            log.info("Dirección existente encontrada para usuario {}: {}", 
                    userId, existingAddress.get().getAddress());
            return existingAddress.get().getAddress();
        } else {
            try {
                // Verificar si ya existe una wallet en el contrato
                String existingWallet = ((com.case_wallet.apirest.infrastructure.blockchain.BlockchainServiceImpl) blockchainService)
                        .getExistingWallet(userInternalId);
                
                if (existingWallet != null) {
                    // La wallet existe en el contrato pero no en nuestra DB, sincronizar
                    log.info("Wallet encontrada en contrato para usuario {}: {}", userId, existingWallet);
                    
                    UserCryptoAddress newCryptoAddress = UserCryptoAddress.builder()
                            .userId(userInternalId)
                            .currency(currency)
                            .address(existingWallet)
                            .isActive(true)
                            .build();
                    
                    addressRepository.save(newCryptoAddress);
                    return existingWallet;
                }
                
                // Crear nueva dirección en la blockchain
                String newAddress = blockchainService.createDepositWallet(userInternalId);
                
                // Validar que la dirección es válida
                if (!blockchainService.isValidAddress(newAddress)) {
                    throw new RuntimeException("La dirección generada no es válida: " + newAddress);
                }
                
                // Guardar en la base de datos
                UserCryptoAddress newCryptoAddress = UserCryptoAddress.builder()
                        .userId(userInternalId)
                        .currency(currency)
                        .address(newAddress)
                        .isActive(true)
                        .build();
                
                addressRepository.save(newCryptoAddress);
                
                log.info("Nueva dirección creada para usuario {}: {}", userId, newAddress);
                return newAddress;
                
            } catch (Exception e) {
                log.error("Error al crear dirección de depósito para usuario {}: {}", userId, e.getMessage(), e);
                throw new RuntimeException("Error al crear dirección de depósito: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public CryptoWithdrawResponse requestCryptoWithdrawal(UUID userId, CryptoWithdrawRequest request) {
        log.info("Procesando solicitud de retiro para usuario {}: {} {}", 
                userId, request.getAmount(), request.getCurrency());
        
        // Validar que el usuario existe
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar PIN
        if (!passwordEncoder.matches(request.getPin(), user.getPinHash())) {
            throw new IllegalArgumentException("PIN incorrecto");
        }
        
        // Validar moneda soportada
        if (!"USDT".equals(request.getCurrency())) {
            throw new IllegalArgumentException("Moneda no soportada: " + request.getCurrency());
        }
        
        // Validar dirección de destino
        if (!blockchainService.isValidAddress(request.getDestinationAddress())) {
            throw new IllegalArgumentException("Dirección de destino inválida");
        }
        
        // Verificar saldo suficiente
        WalletBalanceEntity balance = walletRepositoryPort.findBalanceByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Balance no encontrado"));
        
        BigDecimal availableBalance = balance.getBalanceUSDT();
        if (availableBalance.compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente. Disponible: " + availableBalance);
        }
        
        // DEBITAR saldo inmediatamente (parte crítica del flujo off-chain)
        balance.setBalanceUSDT(balance.getBalanceUSDT().subtract(request.getAmount()));
        walletRepositoryPort.saveBalance(balance);
        
        // Crear registro de retiro como PENDING
        Long userInternalId = getUserInternalId(userId);
        CryptoWithdrawal withdrawal = CryptoWithdrawal.builder()
                .userId(userInternalId)
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .destinationAddress(request.getDestinationAddress())
                .status("PENDING")
                .build();
        
        CryptoWithdrawal savedWithdrawal = withdrawalRepository.save(withdrawal);
        
        log.info("Retiro creado exitosamente: ID={}, Usuario={}, Monto={} {}", 
                savedWithdrawal.getId(), userId, request.getAmount(), request.getCurrency());
        
        return CryptoWithdrawResponse.builder()
                .withdrawalId(savedWithdrawal.getId())
                .currency(savedWithdrawal.getCurrency())
                .amount(savedWithdrawal.getAmount())
                .destinationAddress(savedWithdrawal.getDestinationAddress())
                .status(savedWithdrawal.getStatus())
                .createdAt(savedWithdrawal.getCreatedAt())
                .estimatedCompletion(LocalDateTime.now().plusMinutes(30)) // Estimación
                .message("Retiro en proceso. Se completará en aproximadamente 30 minutos.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CryptoWithdrawResponse> getWithdrawalHistory(UUID userId) {
        Long userInternalId = getUserInternalId(userId);
        List<CryptoWithdrawal> withdrawals = withdrawalRepository.findByUserIdOrderByCreatedAtDesc(userInternalId);
        
        return withdrawals.stream()
                .map(this::mapToWithdrawResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CryptoWithdrawResponse getWithdrawalStatus(UUID userId, Long withdrawalId) {
        Long userInternalId = getUserInternalId(userId);
        CryptoWithdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Retiro no encontrado"));
        
        // Verificar que el retiro pertenece al usuario
        if (!withdrawal.getUserId().equals(userInternalId)) {
            throw new IllegalArgumentException("Retiro no encontrado para este usuario");
        }
        
        return mapToWithdrawResponse(withdrawal);
    }

    private CryptoWithdrawResponse mapToWithdrawResponse(CryptoWithdrawal withdrawal) {
        String message = switch (withdrawal.getStatus()) {
            case "PENDING" -> "Retiro en cola de procesamiento";
            case "PROCESSING" -> "Retiro siendo procesado en la blockchain";
            case "COMPLETED" -> "Retiro completado exitosamente";
            case "FAILED" -> "Retiro falló: " + withdrawal.getErrorMessage();
            default -> "Estado desconocido";
        };
        
        return CryptoWithdrawResponse.builder()
                .withdrawalId(withdrawal.getId())
                .currency(withdrawal.getCurrency())
                .amount(withdrawal.getAmount())
                .destinationAddress(withdrawal.getDestinationAddress())
                .status(withdrawal.getStatus())
                .transactionHash(withdrawal.getTransactionHash())
                .createdAt(withdrawal.getCreatedAt())
                .message(message)
                .build();
    }

    private Long getUserInternalId(UUID userId) {
        return Math.abs((long) userId.hashCode());
    }
}
