package com.case_wallet.apirest.application.wallet.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class GenerateWalletRequestDTO {
    private UUID userId;
} 