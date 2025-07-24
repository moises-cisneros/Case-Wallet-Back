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
@Builder
@Table(name = "wallet_balance")
public class WalletBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "balance_bs", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balanceBS = BigDecimal.ZERO;

    @Column(name = "balance_usdt", precision = 15, scale = 8)
    @Builder.Default
    private BigDecimal balanceUSDT = BigDecimal.ZERO;

    @Column(name = "exchange_rate", precision = 10, scale = 4)
    @Builder.Default
    private BigDecimal exchangeRate = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
