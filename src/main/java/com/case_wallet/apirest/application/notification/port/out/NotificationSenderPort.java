package com.case_wallet.apirest.application.notification.port.out;

import com.case_wallet.apirest.domain.notification.model.Notification;

public interface NotificationSenderPort {
    void sendNotification(String phoneNumber, Notification notification);
    void sendBulkNotifications(Notification notification);
    void sendTransactionNotification(String phoneNumber, String transactionId, String message);
}
