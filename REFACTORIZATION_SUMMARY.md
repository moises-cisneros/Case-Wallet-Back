# Refactorización Completa - Case Wallet API

## Resumen de Cambios Implementados

### 1. Base de Datos

- **V2__add_sms_auth_tables.sql**: Migración para soportar autenticación por SMS
  - Actualizada tabla `users` con campos `phone_number`, `password_hash`, `pin_hash`
  - Nueva tabla `sms_verification` para códigos OTP
  - Nueva tabla `wallet_balance` para balances de usuarios
  - Nuevas tablas `deposits` y `transfers` para transacciones

### 2. Entidades Actualizadas

- **UserEntity**: Modificada para usar `phone_number` como identificador principal
- **SmsVerificationEntity**: Nueva entidad para verificación SMS
- **WalletBalanceEntity**: Nueva entidad para balances
- **DepositEntity**: Nueva entidad para depósitos
- **TransferEntity**: Nueva entidad para transferencias

### 3. DTOs Implementados

#### Autenticación

- `RequestSmsRequest`: Solicitud de código SMS
- `VerifySmsRequest`: Verificación de código SMS
- `CompleteRegistrationRequest`: Completar registro
- `PhoneLoginRequest`: Login con teléfono
- `RefreshTokenRequest/Response`: Renovación de tokens

#### Wallet

- `WalletBalance`: Balance del wallet
- `DepositRequest/Response`: Solicitudes de depósito
- `TransferRequest/Response`: Transferencias
- `TransactionDTO`: Transacciones

### 4. Casos de Uso Implementados

#### SmsAuthUseCase

- `requestSms()`: Envía código OTP por SMS
- `verifySms()`: Verifica código OTP
- `completeRegistration()`: Completa registro de usuario
- `loginWithPhone()`: Login con teléfono y contraseña
- `refreshToken()`: Renueva tokens de autenticación

#### WalletManagementUseCase

- `getBalance()`: Obtiene balance del usuario
- `requestDeposit()`: Solicita depósito
- `transferFunds()`: Transfiere fondos
- `getTransactions()`: Obtiene historial de transacciones

### 5. Controladores REST

#### SmsAuthController (`/auth`)

- `POST /auth/register/request-sms`: Solicitar SMS
- `POST /auth/register/verify-sms`: Verificar SMS
- `POST /auth/register/complete`: Completar registro
- `POST /auth/login`: Login con teléfono
- `POST /auth/refresh`: Renovar token

#### WalletController (`/wallet`)

- `GET /wallet/balance`: Obtener balance
- `POST /wallet/deposit/request`: Solicitar depósito
- `POST /wallet/transfer`: Transferir fondos
- `GET /wallet/transactions`: Obtener transacciones

### 6. Servicios de Infraestructura

- **SmsServiceImpl**: Implementación simulada de servicio SMS
- **SecurityService**: Servicio para obtener usuario actual
- **UserDetailsServiceImpl**: Actualizado para soportar phone_number
- **SmsVerificationAdapter**: Adapter para persistencia SMS
- **WalletRepositoryAdapter**: Adapter para persistencia wallet

### 7. Repositorios Actualizados

- **JpaUserRepository**: Agregados métodos para `findByPhoneNumber`
- **JpaSmsVerificationRepository**: Nuevo repositorio para SMS
- **JpaWalletBalanceRepository**: Nuevo repositorio para balances
- **JpaDepositRepository**: Nuevo repositorio para depósitos
- **JpaTransferRepository**: Nuevo repositorio para transferencias

### 8. Modelos de Dominio Actualizados

- **User**: Agregados campos `phoneNumber`, `passwordHash`, `pinHash`, `createdAt`
- **AuthResponse**: Actualizado con estructura `UserInfo`

## Endpoints Implementados (Según Especificación)

### Autenticación

✅ `POST /auth/register/request-sms`
✅ `POST /auth/register/verify-sms`  
✅ `POST /auth/register/complete`
✅ `POST /auth/login`
✅ `POST /auth/refresh`

### Wallet

✅ `GET /wallet/balance`
✅ `POST /wallet/deposit/request`
✅ `POST /wallet/transfer`
✅ `GET /wallet/transactions`

## Estructura de Respuesta API

Todos los endpoints usan la estructura `ApiResponse<T>`:

```json
{
  "success": boolean,
  "data": T | null,
  "message": string | null,
  "error": string | null
}
```

## Notas de Implementación

### Seguridad

- JWT tokens para autenticación
- Passwords y PINs hasheados con BCrypt
- Validación de permisos con Spring Security

### Transacciones

- Operaciones críticas marcadas con `@Transactional`
- Validación de saldos antes de transferencias
- Estados de transacciones (PENDING, COMPLETED, FAILED)

### Validación

- Validación de entrada con Bean Validation
- Patterns específicos para teléfonos (8 dígitos) y PINs (4 dígitos)
- Validación de balances y montos

### Manejo de Errores

- Exceptions manejadas en controladores
- Logging detallado para debugging
- Respuestas de error estructuradas

## Próximos Pasos Recomendados

1. **Integración SMS Real**: Integrar con Twilio u otro proveedor
2. **Mejoras de Seguridad**: Implementar rate limiting para SMS
3. **Transacciones Blockchain**: Integrar con Mantle Network
4. **Testing**: Agregar tests unitarios e integración
5. **Documentación**: Generar documentación OpenAPI/Swagger
6. **Monitoreo**: Implementar métricas y logs estructurados

## Comandos para Probar

```bash
# Solicitar SMS
curl -X POST http://localhost:8080/auth/register/request-sms \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "12345678"}'

# Verificar SMS
curl -X POST http://localhost:8080/auth/register/verify-sms \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "12345678", "otpCode": "123456"}'

# Completar registro
curl -X POST http://localhost:8080/auth/register/complete \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "12345678", "password": "password123", "pin": "1234"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "12345678", "password": "password123"}'

# Obtener balance (requiere token)
curl -X GET http://localhost:8080/wallet/balance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
