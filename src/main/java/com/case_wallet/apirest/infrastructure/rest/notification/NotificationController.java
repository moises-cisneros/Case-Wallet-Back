package com.case_wallet.apirest.infrastructure.rest.notification;

import com.case_wallet.apirest.application.notification.port.in.NotificationUseCase;
import com.case_wallet.apirest.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    /*
    private final NotificationUseCase notificationUseCase;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Notification>> getNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationUseCase.getNotifications(
            securityService.getCurrentUserId(),
            pageable
        ));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationUseCase.markAsRead(securityService.getCurrentUserId(), notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/fcm-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateFCMToken(@RequestBody String token) {
        notificationUseCase.updateFCMToken(securityService.getCurrentUserId(), token);
        return ResponseEntity.ok().build();
    }*/
}
