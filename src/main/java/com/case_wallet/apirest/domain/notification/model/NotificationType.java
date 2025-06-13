package com.case_wallet.apirest.domain.notification.model;

public enum NotificationType {
    TRANSACTION,       // Notificaciones relacionadas con transacciones
    KYC_UPDATE,       // Actualizaciones del estado KYC
    SECURITY,         // Alertas de seguridad
    SYSTEM,           // Mensajes del sistema
    WALLET            // Actualizaciones relacionadas con la wallet
}
