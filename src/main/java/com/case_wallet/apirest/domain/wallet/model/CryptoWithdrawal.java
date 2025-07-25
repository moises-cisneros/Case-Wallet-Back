package com.case_wallet.apirest.domain.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una solicitud de retiro de criptomonedas
 */
@Entity
@Table(name = "crypto_withdrawals",
       indexes = {
           @Index(name = "idx_user_status", columnList = "user_id, status"),
           @Index(name = "idx_status_created", columnList = "status, created_at"),
           @Index(name = "idx_tx_hash", columnList = "transaction_hash")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoWithdrawal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "currency", nullable = false, length = 10)
    private String currency; // USDT, ETH, etc.
    
    @Column(name = "amount", nullable = false, precision = 18, scale = 8)
    private BigDecimal amount; // Cantidad a retirar
    
    @Column(name = "destination_address", nullable = false, length = 42)
    private String destinationAddress; // Dirección de destino
    
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, PROCESSING, COMPLETED, FAILED
    
    @Column(name = "transaction_hash", length = 66)
    private String transactionHash; // Hash de la transacción on-chain
    
    @Column(name = "gas_fee", precision = 18, scale = 8)
    private BigDecimal gasFee; // Fee pagado por gas
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "error_message", length = 500)
    private String errorMessage; // En caso de error
    
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    public void markAsProcessing() {
        this.status = "PROCESSING";
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted(String txHash) {
        this.status = "COMPLETED";
        this.transactionHash = txHash;
        this.completedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String error) {
        this.status = "FAILED";
        this.errorMessage = error;
        this.retryCount++;
    }
}
