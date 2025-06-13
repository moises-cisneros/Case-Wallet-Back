package com.case_wallet.apirest.domain.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;

    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
}
