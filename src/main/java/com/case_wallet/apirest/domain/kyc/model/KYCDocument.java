package com.case_wallet.apirest.domain.kyc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KYCDocument {
    private UUID id;
    private UUID userId;
    private String documentType;
    private String documentNumber;
    private String documentUrl;
    private String selfieUrl;
    private LocalDateTime submittedAt;
    private String status;
    private String comments;
}
