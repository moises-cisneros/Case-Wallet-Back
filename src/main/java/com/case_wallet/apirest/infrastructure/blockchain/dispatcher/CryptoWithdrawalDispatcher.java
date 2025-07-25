package com.case_wallet.apirest.infrastructure.blockchain.dispatcher;

import com.case_wallet.apirest.application.wallet.service.BlockchainService;
import com.case_wallet.apirest.domain.wallet.model.CryptoWithdrawal;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.CryptoWithdrawalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dispatcher que procesa retiros de criptomonedas de forma asíncrona
 * Implementa el flujo: OFF-CHAIN → ON-CHAIN
 */
@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class CryptoWithdrawalDispatcher {

    private final CryptoWithdrawalRepository withdrawalRepository;
    private final BlockchainService blockchainService;
    
    @Value("${crypto.withdrawal.max-retries:3}")
    private Integer maxRetries;
    
    @Value("${crypto.withdrawal.processing-timeout-minutes:30}")
    private Integer processingTimeoutMinutes;

    /**
     * Procesa retiros pendientes cada 30 segundos
     */
    @Scheduled(fixedDelay = 30000) // 30 segundos
    @Async
    public void processePendingWithdrawals() {
        try {
            List<CryptoWithdrawal> pendingWithdrawals = withdrawalRepository.findPendingWithdrawals();
            
            if (!pendingWithdrawals.isEmpty()) {
                log.info("Procesando {} retiros pendientes", pendingWithdrawals.size());
                
                for (CryptoWithdrawal withdrawal : pendingWithdrawals) {
                    processWithdrawal(withdrawal);
                }
            }
            
        } catch (Exception e) {
            log.error("Error en el dispatcher de retiros: {}", e.getMessage(), e);
        }
    }

    /**
     * Revisa retiros que están "stuck" en procesamiento cada 5 minutos
     */
    @Scheduled(fixedDelay = 300000) // 5 minutos
    @Async
    public void checkStuckWithdrawals() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(processingTimeoutMinutes);
            List<CryptoWithdrawal> stuckWithdrawals = withdrawalRepository.findStuckWithdrawals(cutoffTime);
            
            if (!stuckWithdrawals.isEmpty()) {
                log.warn("Encontrados {} retiros atascados", stuckWithdrawals.size());
                
                for (CryptoWithdrawal withdrawal : stuckWithdrawals) {
                    log.warn("Retiro atascado: ID={}, creado={}", 
                            withdrawal.getId(), withdrawal.getCreatedAt());
                    
                    // Marcar como fallido y reintentar si es posible
                    withdrawal.markAsFailed("Timeout en procesamiento");
                    withdrawalRepository.save(withdrawal);
                    
                    if (withdrawal.getRetryCount() < maxRetries) {
                        withdrawal.setStatus("PENDING");
                        withdrawalRepository.save(withdrawal);
                        log.info("Retiro {} marcado para reintento", withdrawal.getId());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Error verificando retiros atascados: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa un retiro individual
     */
    @Transactional
    protected void processWithdrawal(CryptoWithdrawal withdrawal) {
        try {
            log.info("Procesando retiro: ID={}, Usuario={}, Monto={} {}", 
                    withdrawal.getId(), withdrawal.getUserId(), 
                    withdrawal.getAmount(), withdrawal.getCurrency());
            
            // Marcar como en procesamiento
            withdrawal.markAsProcessing();
            withdrawalRepository.save(withdrawal);
            
            // Aquí iría la lógica real de envío a la blockchain
            // Por ahora simulamos el procesamiento
            String txHash = processBlockchainWithdrawal(withdrawal);
            
            // Marcar como completado
            withdrawal.markAsCompleted(txHash);
            withdrawalRepository.save(withdrawal);
            
            log.info("Retiro completado exitosamente: ID={}, TxHash={}", 
                    withdrawal.getId(), txHash);
            
        } catch (Exception e) {
            log.error("Error procesando retiro {}: {}", withdrawal.getId(), e.getMessage(), e);
            
            // Marcar como fallido
            withdrawal.markAsFailed(e.getMessage());
            withdrawalRepository.save(withdrawal);
            
            // Si no hemos alcanzado el máximo de reintentos, programar para reintento
            if (withdrawal.getRetryCount() < maxRetries) {
                withdrawal.setStatus("PENDING");
                withdrawalRepository.save(withdrawal);
                log.info("Retiro {} programado para reintento ({}/{})", 
                        withdrawal.getId(), withdrawal.getRetryCount(), maxRetries);
            } else {
                log.error("Retiro {} falló definitivamente después de {} intentos", 
                        withdrawal.getId(), maxRetries);
            }
        }
    }

    /**
     * Procesa el retiro en la blockchain
     * TODO: Implementar lógica real cuando esté disponible el contrato
     */
    private String processBlockchainWithdrawal(CryptoWithdrawal withdrawal) throws Exception {
        log.info("Enviando {} {} a la dirección {} en la blockchain", 
                withdrawal.getAmount(), withdrawal.getCurrency(), withdrawal.getDestinationAddress());
        
        // TODO: Implementar lógica real:
        // 1. Construir transacción
        // 2. Firmar con hot wallet (multi-sig)
        // 3. Enviar a la red
        // 4. Esperar confirmación
        // 5. Devolver hash de transacción
        
        // Por ahora simulamos con un delay y hash falso
        Thread.sleep(5000); // Simula tiempo de procesamiento
        
        // Verificar que la dirección sea válida antes de "enviar"
        if (!blockchainService.isValidAddress(withdrawal.getDestinationAddress())) {
            throw new RuntimeException("Dirección de destino inválida: " + withdrawal.getDestinationAddress());
        }
        
        // Generar hash simulado
        String simulatedTxHash = "0x" + Long.toHexString(System.currentTimeMillis()) + 
                                withdrawal.getId().toString();
        
        log.info("Transacción simulada enviada: {}", simulatedTxHash);
        return simulatedTxHash;
    }

    /**
     * Obtiene estadísticas del dispatcher
     */
    public WithdrawalStats getStats() {
        long pending = withdrawalRepository.findPendingWithdrawals().size();
        long processing = withdrawalRepository.findStuckWithdrawals(
                LocalDateTime.now().minusHours(1)).size();
        
        return WithdrawalStats.builder()
                .pendingCount(pending)
                .processingCount(processing)
                .maxRetries(maxRetries)
                .processingTimeoutMinutes(processingTimeoutMinutes)
                .build();
    }

    /**
     * Clase para estadísticas del dispatcher
     */
    public static class WithdrawalStats {
        public final long pendingCount;
        public final long processingCount;
        public final int maxRetries;
        public final int processingTimeoutMinutes;
        
        public static WithdrawalStatsBuilder builder() {
            return new WithdrawalStatsBuilder();
        }
        
        private WithdrawalStats(long pendingCount, long processingCount, 
                               int maxRetries, int processingTimeoutMinutes) {
            this.pendingCount = pendingCount;
            this.processingCount = processingCount;
            this.maxRetries = maxRetries;
            this.processingTimeoutMinutes = processingTimeoutMinutes;
        }
        
        public static class WithdrawalStatsBuilder {
            private long pendingCount;
            private long processingCount;
            private int maxRetries;
            private int processingTimeoutMinutes;
            
            public WithdrawalStatsBuilder pendingCount(long pendingCount) {
                this.pendingCount = pendingCount;
                return this;
            }
            
            public WithdrawalStatsBuilder processingCount(long processingCount) {
                this.processingCount = processingCount;
                return this;
            }
            
            public WithdrawalStatsBuilder maxRetries(int maxRetries) {
                this.maxRetries = maxRetries;
                return this;
            }
            
            public WithdrawalStatsBuilder processingTimeoutMinutes(int processingTimeoutMinutes) {
                this.processingTimeoutMinutes = processingTimeoutMinutes;
                return this;
            }
            
            public WithdrawalStats build() {
                return new WithdrawalStats(pendingCount, processingCount, maxRetries, processingTimeoutMinutes);
            }
        }
    }
}
