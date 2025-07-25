package com.case_wallet.apirest.application.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de dirección de depósito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAddressResponse {
    
    private String currency;
    private String address;
    private String qrCode; // URL o base64 del código QR (opcional)
    private String network; // Nombre de la red (ej: "Mantle Sepolia")
    private String status; // ACTIVE, INACTIVE, etc.
}
