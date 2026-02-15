# CoreBank

CoreBank is a Spring Boot-based core banking backend that manages customers, accounts, cards, transactions, and loans, with OTP + Keycloak authentication and Redis caching.

## Key Features

- Customer registration and profile management
- Account creation (admin role)
- Financial operations: deposit, withdrawal, transfer
- Card issuance for existing accounts
- Loan lifecycle: request, repayment, deletion
- Basic admin dashboard/report endpoint
- Two-step authentication (credentials + OTP)
- Actuator endpoints for runtime monitoring

## Tech Stack

- Java 21
- Spring Boot (Web, Data JPA, Validation, Security, OAuth2 Resource Server)
- PostgreSQL
- Redis
- Keycloak
- Thymeleaf (customer registration form)
- Maven
- Docker Compose

## Project Structure

```text
src/main/java/ir/tejaratBank/core/CoreBank
├── controller   # REST controllers
├── service      # business logic
├── data
│   ├── model    # JPA entities
│   ├── repository
│   └── dto
├── config       # Redis/Security configuration
├── exception    # domain and global exceptions
└── aspect       # AOP for transaction performance logging
```

## Prerequisites

- JDK 21
- Maven (or Maven Wrapper `./mvnw`)
- Docker + Docker Compose

## Quick Start

### 1) Start infrastructure

```bash
docker compose up -d postgres redis redis-insight keycloak
```

Available ports:
- PostgreSQL: `localhost:5433`
- Redis: `localhost:6379`
- Redis Insight: `localhost:5540`
- Keycloak: `localhost:8180`

### 2) Run the application

```bash
./mvnw spring-boot:run
```

Optional build first:

```bash
./mvnw clean package -DskipTests
```

### 3) Health check

```bash
curl http://localhost:8080/actuator/health
```

## Important Configuration

Main configuration is in `src/main/resources/application.properties`.

Key values:
- `spring.datasource.url=jdbc:postgresql://localhost:5433/corebank`
- `spring.data.redis.host=localhost`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/tejarat-realm`
- `management.endpoints.web.exposure.include=health,info,metrics`

## Authentication Flow (Login + OTP)

1. Client sends username/password to `/auth/login`.
2. Credentials are validated against Keycloak.
3. Temporary `loginId`, token/session data are stored in Redis.
4. OTP is sent via `/auth/send-otp`.
5. Client verifies OTP using `/auth/verify-otp` and receives the real access token.

## Main API Endpoints

### Customer
- `GET /customers/register` - show registration form
- `POST /customers/register` - register customer

### Account
- `POST /api/accounts/create?customerId={id}&type={SAVING|CURRENT}`
- `GET /api/accounts/my-details`

### Transaction
- `POST /api/transactions/deposit`
- `POST /api/transactions/withdraw`
- `POST /api/transactions/transfer`
- `GET /api/transactions/{accountId}`

Example transaction payload:

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 1500000,
  "description": "salary transfer"
}
```

### Card
- `POST /api/cards/issue?accountId={id}&pin={1234}`

### Loan
- `POST /api/loans/request?customerId={id}&amount={value}&installments={count}&interestRate={rate}`
- `POST /api/loans/repay?loanId={id}&sourceAccountId={id}&amount={value}`
- `DELETE /api/loans/{id}`

### Admin
- `GET /api/admin/dashboard?requesterId={id}`
- `POST /api/admin/promote?customerId={id}`

## Development Notes

- Centralized exception handling is implemented in `GlobalExceptionHandler`.
- Local development currently uses `app.security.enabled=false` from properties.
- For planned next steps, see `CONTINUATION_PLAN.md`.

## License & Contribution

No explicit project license is currently defined.

To contribute:
1. Open an issue for discussion.
2. Create a feature branch.
3. Run build/tests locally before opening a PR.
