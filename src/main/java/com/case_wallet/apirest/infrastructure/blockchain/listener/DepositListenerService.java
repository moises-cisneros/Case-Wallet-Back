package com.case_wallet.apirest.infrastructure.blockchain.listener;

import com.case_wallet.apirest.domain.wallet.model.UserCryptoAddress;
import com.case_wallet.apirest.infrastructure.blockchain.contracts.USDTContract;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.UserCryptoAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Servicio que escucha los eventos de transferencia de USDT en la blockchain
 * para detectar depósitos automáticamente
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class DepositListenerService {

    private final Web3j web3j;
    private final UserCryptoAddressRepository addressRepository;
    private USDTContract usdtContract;
    
    @Value("${blockchain.usdt.contract.address}")
    private String usdtContractAddress;
    
    private volatile boolean isListening = false;
    private BigInteger lastProcessedBlock = BigInteger.ZERO;

    /**
     * Inicia el listener cuando la aplicación está lista
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        if (isListening) {
            return;
        }
        
        if ("0x...".equals(usdtContractAddress)) {
            log.warn("Dirección del contrato USDT no configurada. Listener de depósitos deshabilitado.");
            return;
        }
        
        try {
            log.info("Iniciando listener de depósitos USDT en contrato: {}", usdtContractAddress);
            
            // Cargar contrato USDT usando ReadonlyTransactionManager
            org.web3j.tx.ReadonlyTransactionManager readonlyTxManager = 
                    new org.web3j.tx.ReadonlyTransactionManager(web3j, "0x0000000000000000000000000000000000000000");
            usdtContract = USDTContract.load(usdtContractAddress, web3j, readonlyTxManager, new DefaultGasProvider());
            
            startDepositListener();
            isListening = true;
        } catch (Exception e) {
            log.error("Error al iniciar el listener de depósitos: {}", e.getMessage(), e);
        }
    }

    /**
     * Configura y ejecuta el listener de eventos Transfer usando polling
     */
    private void startDepositListener() {
        try {
            // Cargar contrato USDT usando ReadonlyTransactionManager
            org.web3j.tx.ReadonlyTransactionManager readonlyTxManager = 
                    new org.web3j.tx.ReadonlyTransactionManager(web3j, "0x0000000000000000000000000000000000000000");
            usdtContract = USDTContract.load(usdtContractAddress, web3j, readonlyTxManager, new DefaultGasProvider());
            
            // Obtener el bloque actual para empezar desde ahí
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
            lastProcessedBlock = currentBlock;
            
            log.info("Listener de depósitos USDT iniciado exitosamente desde bloque: {}", currentBlock);
            
        } catch (Exception e) {
            log.error("Error al configurar el listener: {}", e.getMessage(), e);
            // No relanzar excepción para que la aplicación pueda continuar
        }
    }

    /**
     * Método programado que busca nuevos eventos Transfer cada 30 segundos
     */
    @Scheduled(fixedDelay = 30000) // 30 segundos
    public void pollForDeposits() {
        if (!isListening || usdtContract == null) {
            return;
        }
        
        try {
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
            
            if (currentBlock.compareTo(lastProcessedBlock) > 0) {
                log.debug("Buscando eventos Transfer desde bloque {} hasta {}", lastProcessedBlock.add(BigInteger.ONE), currentBlock);
                
                // Buscar eventos Transfer en el rango de bloques
                EthFilter filter = new EthFilter(
                        DefaultBlockParameter.valueOf(lastProcessedBlock.add(BigInteger.ONE)),
                        DefaultBlockParameter.valueOf(currentBlock),
                        usdtContractAddress
                );
                
                EthLog ethLog = web3j.ethGetLogs(filter).send();
                
                // Procesar cada log encontrado
                for (EthLog.LogResult<?> logResult : ethLog.getLogs()) {
                    if (logResult instanceof EthLog.LogObject) {
                        EthLog.LogObject logObject = (EthLog.LogObject) logResult;
                        processLogForTransferEvent(logObject.get());
                    }
                }
                
                lastProcessedBlock = currentBlock;
            }
            
        } catch (Exception e) {
            log.error("Error al buscar eventos de depósito: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa un log para extraer eventos Transfer
     */
    private void processLogForTransferEvent(org.web3j.protocol.core.methods.response.Log logEntry) {
        try {
            // Verificar si es un evento Transfer (primer topic debe coincidir)
            if (logEntry.getTopics().size() >= 3) {
                USDTContract.TransferEventResponse transferEvent = USDTContract.getTransferEventFromLog(logEntry);
                processTransferEvent(transferEvent);
            }
        } catch (Exception e) {
            log.debug("Log no es un evento Transfer válido: {}", e.getMessage());
        }
    }

    /**
     * Procesa un evento Transfer detectado
     */
    private void processTransferEvent(USDTContract.TransferEventResponse transferEvent) {
        try {
            log.debug("Evento Transfer detectado: {} USDT de {} a {}", 
                    formatUSDT(transferEvent.value), transferEvent.from, transferEvent.to);
            
            // Verificar si la dirección de destino es una de nuestras addresses
            Optional<UserCryptoAddress> userAddress = addressRepository
                    .findByAddressAndIsActive(transferEvent.to, true);
            
            if (userAddress.isPresent()) {
                log.info("Depósito detectado para usuario {}: {} USDT (Tx: {})", 
                        userAddress.get().getUserId(), 
                        formatUSDT(transferEvent.value),
                        transferEvent.log.getTransactionHash());
                
                // Acreditar el depósito
                creditDeposit(userAddress.get(), transferEvent.value, transferEvent.log.getTransactionHash());
            } else {
                log.debug("Transfer a dirección no reconocida: {}", transferEvent.to);
            }
            
        } catch (Exception e) {
            log.error("Error al procesar evento Transfer: {}", e.getMessage(), e);
        }
    }

    /**
     * Acredita un depósito al balance del usuario
     */
    private void creditDeposit(UserCryptoAddress userAddress, BigInteger amount, String txHash) {
        try {
            // Convertir de unidades USDT (6 decimales) a BigDecimal
            BigDecimal depositAmount = formatUSDT(amount);
            
            log.info("Acreditando {} USDT al usuario {} (Tx: {})", 
                    depositAmount, userAddress.getUserId(), txHash);
            
            // TODO: Necesitamos mejorar esta parte para manejar la conversión userId (Long) → UUID
            // Por ahora solo logueamos la detección
            
            // En la implementación completa:
            // 1. Convertir userId de Long a UUID (según diseño de BD)
            // 2. Buscar WalletBalanceEntity por userId UUID
            // 3. Sumar el depositAmount al balanceUSDT
            // 4. Guardar la transacción en el historial
            // 5. Enviar notificación al usuario
            
            log.info("Depósito detectado correctamente - Implementar acreditación completa");
            
        } catch (Exception e) {
            log.error("Error al acreditar depósito para usuario {}: {}", 
                    userAddress.getUserId(), e.getMessage(), e);
        }
    }

    /**
     * Convierte unidades USDT (6 decimales) a BigDecimal
     */
    private BigDecimal formatUSDT(BigInteger amount) {
        return new BigDecimal(amount).divide(BigDecimal.valueOf(1_000_000));
    }

    /**
     * Detiene el listener
     */
    public void stopListening() {
        isListening = false;
        log.info("Listener de depósitos detenido");
    }
}
