package com.case_wallet.apirest.infrastructure.notification.fcm;

import com.case_wallet.apirest.application.notification.port.out.NotificationPort;
import com.case_wallet.apirest.domain.notification.model.Notification;
import com.case_wallet.apirest.infrastructure.database.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMNotificationAdapter implements NotificationPort {

    private final NotificationRepository notificationRepository;

    @Override
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        log.info("Buscando notificaciones para el usuario: {}", userId);
        // Implementación simulada - retorna una página vacía
        return Page.empty(pageable);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        log.info("Buscando notificación: {}", id);
        return Optional.empty();
    }

    @Override
    public Notification save(Notification notification) {
        log.info("Guardando notificación para el usuario: {}", notification.getUserId());
        // Por ahora solo registramos la acción
        return notification;
    }

    @Override
    public void sendPushNotification(String token, Notification notification) {
        // Implementación simulada - solo registra la acción
        log.info("Simulando envío de notificación push. Token: {}, Mensaje: {}",
                token, notification.getMessage());
    }
}
