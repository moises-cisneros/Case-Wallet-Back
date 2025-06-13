package com.case_wallet.apirest.infrastructure.database.notification.adapter;

import com.case_wallet.apirest.application.notification.mapper.NotificationMapper;
import com.case_wallet.apirest.domain.notification.model.Notification;
import com.case_wallet.apirest.infrastructure.database.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toModel);
    }

    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toModel);
    }

    public Notification save(Notification notification) {
        var entity = notificationMapper.toEntity(notification);
        var savedEntity = notificationRepository.save(entity);
        return notificationMapper.toModel(savedEntity);
    }
}
