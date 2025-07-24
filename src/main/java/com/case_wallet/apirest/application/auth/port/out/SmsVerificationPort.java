package com.case_wallet.apirest.application.auth.port.out;

import com.case_wallet.apirest.infrastructure.database.auth.entity.SmsVerificationEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsVerificationPort {
    SmsVerificationEntity save(SmsVerificationEntity entity);
    Optional<SmsVerificationEntity> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode);
    Optional<SmsVerificationEntity> findLatestValidOtpByPhoneNumber(String phoneNumber, LocalDateTime now);
    Optional<SmsVerificationEntity> findLatestVerifiedSmsByPhoneNumber(String phoneNumber);
    void deleteExpiredOtps(LocalDateTime expirationTime);
}
