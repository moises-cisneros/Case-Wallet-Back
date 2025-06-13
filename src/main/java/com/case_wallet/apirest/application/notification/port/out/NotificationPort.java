package com.case_wallet.apirest.application.notification.port.out;

import com.case_wallet.apirest.domain.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationPort {
    Page<Notification> findByUserId(UUID userId, Pageable pageable);
    Optional<Notification> findById(UUID id);
    Notification save(Notification notification);
    void sendPushNotification(String token, Notification notification);
}
