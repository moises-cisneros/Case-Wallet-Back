-- Actualizar la tabla users para el nuevo flujo
ALTER TABLE users DROP COLUMN IF EXISTS email;
ALTER TABLE users DROP COLUMN IF EXISTS name;
ALTER TABLE users DROP COLUMN IF EXISTS google_id;
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone_number VARCHAR(15) UNIQUE;
ALTER TABLE users ALTER COLUMN phone_number SET NOT NULL;

-- Crear tabla para SMS OTP
CREATE TABLE IF NOT EXISTS sms_verification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(15) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(phone_number, otp_code)
);

-- Crear tabla para wallet balance
CREATE TABLE IF NOT EXISTS wallet_balance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance_bs DECIMAL(15,2) DEFAULT 0.00,
    balance_usdt DECIMAL(15,8) DEFAULT 0.00000000,
    exchange_rate DECIMAL(10,4) DEFAULT 0.0000,
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id)
);

-- Crear tabla para deposits
CREATE TABLE IF NOT EXISTS deposits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    receipt_image_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Crear tabla para transfers (si no existe ya)
CREATE TABLE IF NOT EXISTS transfers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(15,8) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Actualizar tabla transactions para más campos
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS description VARCHAR(255);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS recipient_id UUID REFERENCES users(id);

-- Índices para optimización
CREATE INDEX IF NOT EXISTS idx_sms_verification_phone ON sms_verification(phone_number);
CREATE INDEX IF NOT EXISTS idx_sms_verification_expires ON sms_verification(expires_at);
CREATE INDEX IF NOT EXISTS idx_wallet_balance_user ON wallet_balance(user_id);
CREATE INDEX IF NOT EXISTS idx_deposits_user ON deposits(user_id);
CREATE INDEX IF NOT EXISTS idx_transfers_sender ON transfers(sender_id);
CREATE INDEX IF NOT EXISTS idx_transfers_recipient ON transfers(recipient_id);
