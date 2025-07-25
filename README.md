# Case Wallet - Backend API

## Project Summary

Case Wallet is a digital wallet solution developed for the Bolivian market, focused on providing financial stability by enabling access to USDT via blockchain. The backend is built with hexagonal architecture and provides RESTful APIs for all wallet operations.

-----

## Architecture and Design

### Hexagonal Architecture (Clean Architecture)

The project implements hexagonal architecture with a clear separation of concerns:

- **Application Layer**: Use cases and DTOs
- **Domain Layer**: Entities and business logic
- **Infrastructure Layer**: Technical implementations (REST, DB, Blockchain)

### Technology Stack

- **Java 17** - Primary language
- **Spring Boot 3.5** - Web framework and DI
- **PostgreSQL** - Main database
- **Flyway** - Database migrations
- **Web3j 4.9.8** - Integration with Mantle Blockchain
- **JWT** - Authentication and authorization
- **Twilio** - SMS services
- **Lombok** - Boilerplate code reduction

-----

## Implemented Features

### Authentication System

- ✅ **SMS Registration**: SMS verification using Twilio
- ✅ **PIN Login**: Local authentication with phone number and PIN
- ✅ **JWT Tokens**: Session management with access/refresh tokens
- ❌ **Google OAuth**: Not currently implemented

### Wallet Management

- ✅ **HD Wallets**: Generation using BIP39/BIP44
- ✅ **Deposit Addresses**: Dynamic address creation
- ✅ **Balance Tracking**: USDT balance tracking
- ✅ **P2P Transfers**: Instant off-chain transfers

### Blockchain Integration

- ✅ **Mantle Sepolia**: Testnet integration
- ✅ **Smart Contracts**: DepositWalletFactory and USDT
- ✅ **Deposit Listener**: Automatic transfer detection
- ✅ **Withdrawal System**: Batch processing with a dispatcher

### Additional Features

- ✅ **KYC**: Document upload and validation
- ✅ **Notifications**: Internal notification system
- ✅ **History**: Complete transaction tracking

-----

## Smart Contracts

### DepositWalletFactory

- **Address**: `0x1E694fbdB612Fb57A161b5D76d0f05DB1dbD59Bf`
- **Network**: Mantle Sepolia Testnet
- **Function**: Creates custom deposit addresses for users

### USDT Mock Contract

- **Address**: `0x376428e7f26D5867e69201b275553C45B09EE090`
- **Network**: Mantle Sepolia Testnet
- **Function**: ERC20 token for deposit/withdrawal testing

-----

## API Endpoints

### Authentication (`/api/v1/auth`)

- `POST /register/request-sms` - Request verification code
- `POST /register/verify-sms` - Verify SMS code
- `POST /register/complete` - Complete registration with user data
- `POST /login` - Authenticate with phone/password
- `POST /refresh` - Renew JWT tokens

### Wallets (`/api/v1/wallet`)

- `GET /balance` - Query current balance
- `POST /transfer` - Instant P2P transfer
- `GET /transactions` - Transaction history

### Crypto Operations (`/api/v1/crypto/wallet`)

- `GET /address` - Get existing deposit address
- `POST /address` - Create new deposit address
- `POST /withdraw` - Request withdrawal to external address
- `GET /withdrawals` - Query withdrawal status

### KYC (`/api/v1/kyc`)

- `POST /documents` - Upload identity documents
- `POST /selfie` - Upload selfie photo
- `GET /status` - Query verification status

### User (`/api/v1/users`)

- `GET /{userId}/profile` - Get user profile

### Notifications (`/api/v1/notifications`)

- `GET /` - Get user notifications

-----

## Business Flows

### Registration Flow

1. User requests SMS with phone number
2. System sends code via Twilio
3. User verifies SMS code
4. User completes registration with password, PIN, and data
5. System generates HD wallet and JWT tokens

### Deposit Flow

1. User requests deposit address
2. System creates custom wallet on blockchain
3. User transfers USDT to the address
4. Listener automatically detects transfer
5. Balance is credited in real-time

### P2P Transfer Flow

1. User A requests to transfer to User B
2. System validates balance and PIN
3. Balance is debited from A and credited to B
4. Transaction is recorded instantly
5. Both users receive notification

### Withdrawal Flow

1. User requests withdrawal to external address
2. System validates balance, address, and PIN
3. Request is stored as PENDING
4. Dispatcher processes withdrawals in batches every 5 minutes
5. Transaction is executed on-chain

-----

## Database

### Main Tables

- `users` - Basic user information
- `wallet_balance` - Balances per user and currency
- `crypto_addresses` - Generated deposit addresses
- `crypto_deposits` - Detected blockchain deposits
- `crypto_withdrawals` - Withdrawal requests
- `transfers` - Off-chain P2P transfers
- `sms_verification` - Temporary SMS codes

### Migrations

- `V1__init_schema.sql` - Initial schema with users and wallets
- `V2__add_sms_auth_tables.sql` - SMS authentication and crypto tables
- `-- V3__add_crypto_addresses_support.sql` - Migration to add cryptocurrency deposit address support
- `V4__add_crypto_withdrawals_support` - Migration to add cryptocurrency withdrawal support

-----

## Background Services

### DepositListenerService

- **Function**: Detect incoming USDT transfers
- **Method**: Polling every 30 seconds with `eth_getLogs`
- **Compatibility**: Adapted for Mantle Sepolia RPC

### CryptoWithdrawalDispatcher

- **Function**: Process pending withdrawals in batches
- **Frequency**: Every 5 minutes
- **Security**: Gas and transaction limit validation

-----

## Environment Configuration

### Application Variables

```properties
# Blockchain
blockchain.rpc.url=https://rpc.sepolia.mantle.xyz
blockchain.platform.private.key=${PLATFORM_WALLET_PRIVATE_KEY}
blockchain.deposit.factory.address=0x1E694fbdB612Fb57A161b5D76d0f05DB1dbD59Bf
blockchain.usdt.contract.address=0x376428e7f26D5867e69201b275553C45B09EE090

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# SMS (Twilio)
twilio.account.sid=${TWILIO_ACCOUNT_SID}
twilio.auth.token=${TWILIO_AUTH_TOKEN}
twilio.phone.number=${TWILIO_PHONE_NUMBER}

# JWT
jwt.secret=${JWT_SECRET}
jwt.access.expiration=3600000
jwt.refresh.expiration=86400000
```

-----

## Testing

### Testing Documentation

- `docs/API_TESTING.md` - Complete testing guide with examples
- `docs/Case_Wallet_API.postman_collection.json` - Postman Collection
- `docs/Thunder_Client_Tests.md` - Tests for Thunder Client (VS Code)

### Basic Testing Flow

1. Register user with SMS
2. Complete registration and obtain JWT
3. Query initial balance (0.00)
4. Create deposit address
5. Simulate deposit (manually update DB)
6. Perform P2P transfer
7. Request crypto withdrawal

-----

## Development Status

### Completed

- ✅ Full SMS authentication system
- ✅ Full integration with Mantle Sepolia
- ✅ Wallet operations (deposits, withdrawals, transfers)
- ✅ Blockchain event listener
- ✅ Notification system
- ✅ Basic KYC with file uploads

### Pending

- ⏳ Google OAuth2 integration
- ⏳ Dynamic fees system
- ⏳ Mobile push notifications
- ⏳ Administrative dashboard
- ⏳ Migration to Mantle Mainnet

-----

## Security

### Implemented Measures

- JWT with refresh tokens
- PIN validation for sensitive operations
- Input sanitization and DTO validation
- Secure private key handling
- Rate limiting on critical endpoints
- Audit logs for all transactions

### Production Considerations

- Mandatory HTTPS configuration
- Implement WAF (Web Application Firewall)
- Automatic database backup
- Blockchain transaction monitoring
- Automatic security alerts

-----

- **Case Wallet** - Developed for the Bolivian market with cutting-edge blockchain technology.
