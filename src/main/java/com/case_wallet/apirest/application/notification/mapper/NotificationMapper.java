package com.case_wallet.apirest.application.notification.mapper;

import com.case_wallet.apirest.domain.notification.model.Notification;
import com.case_wallet.apirest.infrastructure.database.notification.entity.NotificationEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toModel(NotificationEntity entity) {
        if (entity == null) return null;

        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationEntity toEntity(Notification model) {
        if (model == null) return null;

        NotificationEntity entity = new NotificationEntity();
        entity.setId(model.getId());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(model.getUserId());
        entity.setUser(userEntity);

        entity.setTitle(model.getTitle());
        entity.setMessage(model.getMessage());
        entity.setType(model.getType());
        entity.setRead(model.isRead());
        entity.setCreatedAt(model.getCreatedAt());

        return entity;
    }
}
