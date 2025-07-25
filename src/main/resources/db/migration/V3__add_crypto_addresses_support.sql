-- Migration para añadir soporte de direcciones de depósito de criptomonedas
-- V3__add_crypto_addresses_support.sql

-- Crear tabla para direcciones de depósito de criptomonedas
CREATE TABLE user_crypto_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    address VARCHAR(42) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Crear índices para optimizar consultas
CREATE UNIQUE INDEX idx_user_crypto_addresses_user_currency 
    ON user_crypto_addresses(user_id, currency);

CREATE UNIQUE INDEX idx_user_crypto_addresses_address 
    ON user_crypto_addresses(address);

CREATE INDEX idx_user_crypto_addresses_currency_active 
    ON user_crypto_addresses(currency, is_active);

-- Añadir comentarios para documentación
COMMENT ON TABLE user_crypto_addresses IS 'Almacena las direcciones de depósito de criptomonedas para cada usuario';
COMMENT ON COLUMN user_crypto_addresses.user_id IS 'ID del usuario propietario de la dirección';
COMMENT ON COLUMN user_crypto_addresses.currency IS 'Tipo de criptomoneda (USDT, ETH, BTC, etc.)';
COMMENT ON COLUMN user_crypto_addresses.address IS 'Dirección de la wallet de depósito en la blockchain';
COMMENT ON COLUMN user_crypto_addresses.created_at IS 'Fecha y hora de creación de la dirección';
COMMENT ON COLUMN user_crypto_addresses.is_active IS 'Indica si la dirección está activa para recibir depósitos';

-- Añadir datos de ejemplo (opcional, remover en producción)
-- INSERT INTO user_crypto_addresses (user_id, currency, address, is_active) 
-- VALUES 
--     (1, 'USDT', '0x1234567890123456789012345678901234567890', true),
--     (2, 'USDT', '0x2345678901234567890123456789012345678901', true);
