package com.case_wallet.apirest.infrastructure.database.auth.adapter;

import com.case_wallet.apirest.application.auth.port.out.SmsVerificationPort;
import com.case_wallet.apirest.infrastructure.database.auth.entity.SmsVerificationEntity;
import com.case_wallet.apirest.infrastructure.database.auth.repository.JpaSmsVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SmsVerificationAdapter implements SmsVerificationPort {

    private final JpaSmsVerificationRepository repository;

    @Override
    public SmsVerificationEntity save(SmsVerificationEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<SmsVerificationEntity> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode) {
        return repository.findByPhoneNumberAndOtpCodeAndIsVerifiedFalse(phoneNumber, otpCode);
    }

    @Override
    public Optional<SmsVerificationEntity> findLatestValidOtpByPhoneNumber(String phoneNumber, LocalDateTime now) {
        return repository.findLatestValidOtpByPhoneNumber(phoneNumber, now);
    }

    @Override
    public Optional<SmsVerificationEntity> findLatestVerifiedSmsByPhoneNumber(String phoneNumber) {
        return repository.findLatestVerifiedSmsByPhoneNumber(phoneNumber);
    }

    @Override
    public void deleteExpiredOtps(LocalDateTime expirationTime) {
        repository.deleteByExpiresAtBefore(expirationTime);
    }
}
