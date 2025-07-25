package com.case_wallet.apirest.infrastructure.blockchain;

import com.case_wallet.apirest.application.wallet.service.BlockchainService;
import com.case_wallet.apirest.infrastructure.blockchain.contracts.DepositWalletFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Implementación del servicio de blockchain para Mantle Network
 */
@Slf4j
@Service
public class BlockchainServiceImpl implements BlockchainService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final String factoryContractAddress;
    private final String usdtContractAddress;
    private final DepositWalletFactory factoryContract;

    public BlockchainServiceImpl(Web3j web3j,
                               @Value("${platform.wallet.private-key}") String privateKey,
                               @Value("${factory.contract.address}") String contractAddress,
                               @Value("${usdt.contract.address}") String usdtAddress) {
        this.web3j = web3j;
        this.credentials = Credentials.create(privateKey);
        this.factoryContractAddress = contractAddress;
        this.usdtContractAddress = usdtAddress;
        this.factoryContract = loadContract(contractAddress);
        
        log.info("BlockchainService inicializado con factory: {} y USDT: {}", 
                contractAddress, usdtAddress);
    }

    @Override
    public String createDepositWallet(Long userId) throws Exception {
        log.info("Creando wallet de depósito para usuario: {}", userId);
        
        try {
            // Llamar al contrato para crear la nueva wallet
            TransactionReceipt receipt = factoryContract.createWallet(BigInteger.valueOf(userId)).send();
            
            if (!receipt.isStatusOK()) {
                throw new RuntimeException("Transacción falló con status: " + receipt.getStatus());
            }
            
            // Extraer la dirección de la nueva wallet del evento WalletCreated
            List<DepositWalletFactory.WalletCreatedEventResponse> events = 
                    DepositWalletFactory.getWalletCreatedEvents(receipt);
            
            if (events.isEmpty()) {
                throw new RuntimeException("No se encontró evento WalletCreated en la transacción");
            }
            
            String newWalletAddress = events.get(0).walletAddress;
            
            log.info("Wallet creada exitosamente para usuario {}: {} (Tx: {})", 
                    userId, newWalletAddress, receipt.getTransactionHash());
            
            return newWalletAddress;
            
        } catch (Exception e) {
            log.error("Error al crear wallet para usuario {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al crear la billetera en la blockchain: " + e.getMessage());
        }
    }

    @Override
    public boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        
        // Verificar que sea una dirección Ethereum válida (42 caracteres, empezando con 0x)
        return address.matches("^0x[a-fA-F0-9]{40}$");
    }

    @Override
    public String getUSDTBalance(String address) throws Exception {
        log.debug("Consultando balance USDT para dirección: {}", address);
        
        try {
            // TODO: Implementar consulta real al contrato USDT cuando esté disponible
            // Por ahora retornamos "0" como placeholder
            return "0";
            
        } catch (Exception e) {
            log.error("Error al consultar balance USDT para {}: {}", address, e.getMessage());
            throw new RuntimeException("Error al consultar balance USDT: " + e.getMessage());
        }
    }

    /**
     * Carga el contrato DepositWalletFactory
     */
    private DepositWalletFactory loadContract(String contractAddress) {
        ContractGasProvider gasProvider = new DefaultGasProvider();
        return DepositWalletFactory.load(contractAddress, web3j, credentials, gasProvider);
    }

    /**
     * Obtiene la dirección de wallet existente para un usuario
     */
    public String getExistingWallet(Long userId) throws Exception {
        try {
            String walletAddress = factoryContract.getWallet(BigInteger.valueOf(userId)).send();
            
            // Si la dirección es 0x0000000000000000000000000000000000000000, no existe wallet
            if ("0x0000000000000000000000000000000000000000".equals(walletAddress)) {
                return null;
            }
            
            return walletAddress;
            
        } catch (Exception e) {
            log.error("Error al consultar wallet existente para usuario {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el balance de ETH de una dirección
     */
    public BigDecimal getETHBalance(String address) throws Exception {
        try {
            EthGetBalance ethGetBalance = web3j
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send();
            
            BigInteger balance = ethGetBalance.getBalance();
            return Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);
            
        } catch (Exception e) {
            log.error("Error al consultar balance ETH para {}: {}", address, e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica la conexión con la red
     */
    public boolean isConnected() {
        try {
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.debug("Conexión verificada. Cliente: {}", clientVersion);
            return true;
        } catch (Exception e) {
            log.error("Error de conexión con la red: {}", e.getMessage());
            return false;
        }
    }
}
