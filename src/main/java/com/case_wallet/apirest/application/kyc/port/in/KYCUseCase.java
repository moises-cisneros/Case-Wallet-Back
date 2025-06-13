package com.case_wallet.apirest.application.kyc.port.in;

import com.case_wallet.apirest.application.kyc.dto.KYCDocumentsRequestDTO;
import com.case_wallet.apirest.domain.kyc.model.KYCDocument;
import com.case_wallet.apirest.domain.user.model.KYCStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface KYCUseCase {
    KYCDocument submitDocuments(UUID userId, KYCDocumentsRequestDTO request);
    void submitSelfie(UUID userId, MultipartFile selfie);
    KYCStatus getKYCStatus(UUID userId);
    KYCDocument updateKYCInformation(UUID userId, KYCDocumentsRequestDTO request);
}
