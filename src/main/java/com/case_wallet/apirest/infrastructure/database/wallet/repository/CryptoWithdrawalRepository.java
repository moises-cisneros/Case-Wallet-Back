package com.case_wallet.apirest.infrastructure.database.wallet.repository;

import com.case_wallet.apirest.domain.wallet.model.CryptoWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para los retiros de criptomonedas
 */
@Repository
public interface CryptoWithdrawalRepository extends JpaRepository<CryptoWithdrawal, Long> {
    
    /**
     * Busca retiros pendientes para procesamiento
     */
    @Query("SELECT cw FROM CryptoWithdrawal cw WHERE cw.status = 'PENDING' ORDER BY cw.createdAt ASC")
    List<CryptoWithdrawal> findPendingWithdrawals();
    
    /**
     * Busca retiros en proceso que podrían haber fallado
     */
    @Query("SELECT cw FROM CryptoWithdrawal cw WHERE cw.status = 'PROCESSING' AND cw.processedAt < :cutoffTime")
    List<CryptoWithdrawal> findStuckWithdrawals(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Busca retiros fallidos que pueden reintentarse
     */
    @Query("SELECT cw FROM CryptoWithdrawal cw WHERE cw.status = 'FAILED' AND cw.retryCount < :maxRetries")
    List<CryptoWithdrawal> findRetryableWithdrawals(@Param("maxRetries") Integer maxRetries);
    
    /**
     * Busca retiros por usuario
     */
    List<CryptoWithdrawal> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Busca retiros por usuario y estado
     */
    List<CryptoWithdrawal> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    
    /**
     * Busca retiro por hash de transacción
     */
    CryptoWithdrawal findByTransactionHash(String transactionHash);
}
