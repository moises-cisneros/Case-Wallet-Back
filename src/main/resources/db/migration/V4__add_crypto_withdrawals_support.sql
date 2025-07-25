-- Migration para añadir soporte de retiros de criptomonedas

-- Crear tabla para retiros de criptomonedas
CREATE TABLE crypto_withdrawals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    amount DECIMAL(18,8) NOT NULL,
    destination_address VARCHAR(42) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_hash VARCHAR(66),
    gas_fee DECIMAL(18,8),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    completed_at TIMESTAMP,
    error_message VARCHAR(500),
    retry_count INTEGER DEFAULT 0
);

-- Crear Tabla de configuración para el dispatcher
CREATE TABLE public.configuration (
    key VARCHAR(100) PRIMARY KEY,
    value VARCHAR(500) NOT NULL,
    description TEXT
);

-- Crear índices para optimizar consultas del dispatcher
CREATE INDEX idx_crypto_withdrawals_user_status 
    ON crypto_withdrawals(user_id, status);

CREATE INDEX idx_crypto_withdrawals_status_created 
    ON crypto_withdrawals(status, created_at);

CREATE INDEX idx_crypto_withdrawals_tx_hash 
    ON crypto_withdrawals(transaction_hash);

-- Añadir comentarios para documentación
COMMENT ON TABLE crypto_withdrawals IS 'Almacena las solicitudes de retiro de criptomonedas';
COMMENT ON COLUMN crypto_withdrawals.user_id IS 'ID del usuario que solicita el retiro';
COMMENT ON COLUMN crypto_withdrawals.currency IS 'Tipo de criptomoneda a retirar (USDT, ETH, etc.)';
COMMENT ON COLUMN crypto_withdrawals.amount IS 'Cantidad a retirar';
COMMENT ON COLUMN crypto_withdrawals.destination_address IS 'Dirección de destino para el retiro';
COMMENT ON COLUMN crypto_withdrawals.status IS 'Estado del retiro (PENDING, PROCESSING, COMPLETED, FAILED)';
COMMENT ON COLUMN crypto_withdrawals.transaction_hash IS 'Hash de la transacción en la blockchain';
COMMENT ON COLUMN crypto_withdrawals.gas_fee IS 'Fee pagado por gas en la transacción';
COMMENT ON COLUMN crypto_withdrawals.created_at IS 'Fecha y hora de creación de la solicitud';
COMMENT ON COLUMN crypto_withdrawals.processed_at IS 'Fecha y hora cuando se inició el procesamiento';
COMMENT ON COLUMN crypto_withdrawals.completed_at IS 'Fecha y hora de completado';
COMMENT ON COLUMN crypto_withdrawals.error_message IS 'Mensaje de error en caso de fallo';
COMMENT ON COLUMN crypto_withdrawals.retry_count IS 'Número de reintentos realizados';

-- Añadir configuración para el dispatcher
INSERT INTO public.configuration (key, value, description) VALUES
('crypto.withdrawal.max-retries', '3', 'Número máximo de reintentos para retiros fallidos'),
('crypto.withdrawal.processing-timeout-minutes', '30', 'Tiempo límite en minutos para considerar un retiro como atascado'),
('crypto.withdrawal.min-amount-usdt', '1.0', 'Cantidad mínima para retiros de USDT')
ON CONFLICT (key) DO NOTHING;
