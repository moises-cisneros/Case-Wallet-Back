package com.case_wallet.apirest.infrastructure.database.wallet.entity;

import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallets")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "balance_local", precision = 15, scale = 2)
    private BigDecimal balanceLocal = BigDecimal.ZERO;

    @Column(name = "balance_crypto", precision = 15, scale = 8)
    private BigDecimal balanceCrypto = BigDecimal.ZERO;

    @Column(name = "crypto_wallet_address")
    private String cryptoWalletAddress;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
