package com.case_wallet.apirest.infrastructure.notification.adapter;

import com.case_wallet.apirest.application.notification.port.out.NotificationSenderPort;
import com.case_wallet.apirest.domain.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultNotificationAdapter implements NotificationSenderPort {

    @Override
    public void sendNotification(String phoneNumber, Notification notification) {
        // Implementación base que solo registra la notificación
        log.info("Notificación enviada a {}: {}", phoneNumber, notification);
    }

    @Override
    public void sendBulkNotifications(Notification notification) {
        // Implementación base para envío masivo
        log.info("Notificación masiva enviada: {}", notification);
    }

    @Override
    public void sendTransactionNotification(String phoneNumber, String transactionId, String message) {
        // Implementación base para notificaciones de transacciones
        log.info("Notificación de transacción {} enviada a {}: {}", transactionId, phoneNumber, message);
    }
}
