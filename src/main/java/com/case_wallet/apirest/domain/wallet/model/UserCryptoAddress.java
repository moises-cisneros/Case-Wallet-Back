package com.case_wallet.apirest.domain.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_crypto_addresses", 
       indexes = {
           @Index(name = "idx_user_currency", columnList = "user_id, currency", unique = true),
           @Index(name = "idx_address", columnList = "address", unique = true)
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCryptoAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "currency", nullable = false, length = 10)
    private String currency; // USDT, ETH, etc.
    
    @Column(name = "address", nullable = false, length = 42)
    private String address; // Dirección de la wallet de depósito
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
