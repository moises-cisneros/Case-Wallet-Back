package com.case_wallet.apirest.application.wallet.service;

/**
 * Servicio para interactuar con la blockchain de Mantle Network
 */
public interface BlockchainService {
    
    /**
     * Crea una nueva wallet de depósito para un usuario en la blockchain
     * @param userId ID del usuario
     * @return dirección de la nueva wallet creada
     * @throws Exception si hay error en la creación
     */
    String createDepositWallet(Long userId) throws Exception;
    
    /**
     * Verifica si una dirección es válida
     * @param address dirección a verificar
     * @return true si es válida
     */
    boolean isValidAddress(String address);
    
    /**
     * Obtiene el balance de USDT en una dirección específica
     * @param address dirección a consultar
     * @return balance en USDT
     * @throws Exception si hay error en la consulta
     */
    String getUSDTBalance(String address) throws Exception;
}
