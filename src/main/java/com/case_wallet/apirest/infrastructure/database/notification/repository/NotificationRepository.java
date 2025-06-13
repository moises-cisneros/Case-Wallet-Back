package com.case_wallet.apirest.infrastructure.database.notification.repository;

import com.case_wallet.apirest.infrastructure.database.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
