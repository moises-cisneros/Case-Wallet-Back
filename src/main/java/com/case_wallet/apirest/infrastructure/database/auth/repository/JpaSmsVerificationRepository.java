package com.case_wallet.apirest.infrastructure.database.auth.repository;

import com.case_wallet.apirest.infrastructure.database.auth.entity.SmsVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaSmsVerificationRepository extends JpaRepository<SmsVerificationEntity, UUID> {
    
    Optional<SmsVerificationEntity> findByPhoneNumberAndOtpCodeAndIsVerifiedFalse(String phoneNumber, String otpCode);
    
    @Query("SELECT s FROM SmsVerificationEntity s WHERE s.phoneNumber = :phoneNumber AND s.isVerified = true AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    Optional<SmsVerificationEntity> findLatestValidOtpByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM SmsVerificationEntity s WHERE s.phoneNumber = :phoneNumber AND s.isVerified = true ORDER BY s.createdAt DESC")
    Optional<SmsVerificationEntity> findLatestVerifiedSmsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    void deleteByExpiresAtBefore(LocalDateTime expirationTime);
}
