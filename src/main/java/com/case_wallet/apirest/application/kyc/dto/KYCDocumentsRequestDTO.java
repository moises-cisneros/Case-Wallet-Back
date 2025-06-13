package com.case_wallet.apirest.application.kyc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KYCDocumentsRequestDTO {
    @NotBlank
    private String documentType;

    @NotBlank
    private String documentNumber;

    @NotNull
    private MultipartFile documentFile;

    @NotNull
    private MultipartFile selfieFile;
}
