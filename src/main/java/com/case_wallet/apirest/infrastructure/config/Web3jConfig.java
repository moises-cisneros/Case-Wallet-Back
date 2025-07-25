package com.case_wallet.apirest.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Slf4j
@Configuration
public class Web3jConfig {

    @Value("${mantle.rpc.url}")
    private String rpcUrl;

    @Value("${mantle.chain.id}")
    private Long chainId;

    /**
     * Bean de Web3j configurado para Mantle Network
     * @return instancia de Web3j
     */
    @Bean
    public Web3j web3j() {
        log.info("Configurando Web3j para Mantle Network - RPC: {}, Chain ID: {}", rpcUrl, chainId);
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        
        // Verificar conexión
        try {
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.info("Conectado exitosamente a Mantle Network. Cliente: {}", clientVersion);
        } catch (Exception e) {
            log.error("Error al conectar con Mantle Network: {}", e.getMessage());
        }
        
        return web3j;
    }
}
