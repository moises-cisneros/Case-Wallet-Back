package com.case_wallet.apirest.infrastructure.rest.kyc;

import com.case_wallet.apirest.application.kyc.dto.KYCDocumentsRequestDTO;
import com.case_wallet.apirest.application.kyc.port.in.KYCUseCase;
import com.case_wallet.apirest.domain.kyc.model.KYCDocument;
import com.case_wallet.apirest.domain.user.model.KYCStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KYCController {
    //private final KYCUseCase kycUseCase;
    //private final SecurityService securityService;

    /*
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KYCDocument> submitDocuments(@Valid @ModelAttribute KYCDocumentsRequestDTO request) {
        return ResponseEntity.ok(kycUseCase.submitDocuments(securityService.getCurrentUserId(), request));
    }

    @PostMapping(value = "/selfie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> submitSelfie(@RequestParam("file") MultipartFile selfie) {
        kycUseCase.submitSelfie(securityService.getCurrentUserId(), selfie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KYCStatus> getStatus() {
        return ResponseEntity.ok(kycUseCase.getKYCStatus(securityService.getCurrentUserId()));
    }

    @PutMapping(value = "/information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KYCDocument> updateInformation(@Valid @ModelAttribute KYCDocumentsRequestDTO request) {
        return ResponseEntity.ok(kycUseCase.updateKYCInformation(securityService.getCurrentUserId(), request));
    }*/
}
