-- Crear tablas iniciales
CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(8) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    kyc_status VARCHAR(20) DEFAULT 'PENDING',
    mantle_address VARCHAR(42),
    created_at TIMESTAMP DEFAULT NOW(),
    id_rol INTEGER REFERENCES role(id),
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN'))
);

-- Insertar roles básicos
INSERT INTO role (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO role (name) VALUES ('USER') ON CONFLICT DO NOTHING;

-- Crear un usuario administrador inicial
INSERT INTO users (id, phone_number, password_hash, pin_hash, kyc_status, id_rol)
VALUES (
    '3b3c6520-a6c5-4233-97ad-b83cfcb8ec09',
    '12345678',
    '$2a$10$vd9wvfJZiMsJypbCYQMMF.6H5flX7Z5F/xjcHJJ5weMeLwrRElBLe',
    '$2a$10$/15HCv32v4S00wbhWuzmauzYiRU4E9aKlqE.1oBrTtoxdB.ZCOZZW',
    'APPROVED',
    (SELECT id FROM role WHERE name = 'ADMIN')
) ON CONFLICT DO NOTHING;
 