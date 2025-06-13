package com.case_wallet.apirest.application.notification.port.in;

import com.case_wallet.apirest.domain.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface NotificationUseCase {
    void sendNotification(UUID userId, String message, String title);
    void markAsRead(UUID userId, UUID notificationId);
    Page<Notification> getUserNotifications(UUID userId, Pageable pageable);
}
