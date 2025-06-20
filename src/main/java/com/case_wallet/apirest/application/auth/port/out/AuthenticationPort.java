package com.case_wallet.apirest.application.auth.port.out;

import com.case_wallet.apirest.domain.user.model.User;

public interface AuthenticationPort {
    /**
     * Genera un token JWT para el usuario
     * @param user Usuario para el que se generará el token
     * @return Token JWT generado
     */
    String generateToken(User user);

    /**
     * Extrae el correo electrónico del token JWT
     * @param token Token JWT del que se extraerá el correo electrónico
     * @return Correo electrónico extraído del token
     */
    String getEmailFromToken(String token);

    /**
     * Valida un token JWT
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Valida un token JWT para un usuario específico
     * @param token Token JWT a validar
     * @param user Usuario contra el que se validará el token
     * @return true si el token es válido para el usuario, false en caso contrario
     */
    boolean validateToken(String token, User user);
}
