# RideLink — Backend Microservices

RideLink is a ride-sharing backend built as **four independently executable Spring Boot microservices**: accounts and authentication, drivers and vehicles, ride lifecycle management, and fares and simulated payments. Each service owns its own database and talks to the others only through REST APIs.

> IT3130 Application Development · Group Assignment

---

## Table of Contents

- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Repository Structure](#repository-structure)
- [Getting Started](#getting-started)
- [Configuration & Environment Variables](#configuration--environment-variables)
- [Authentication (JWT)](#authentication-jwt)
- [API Reference](#api-reference)
- [Ride Lifecycle](#ride-lifecycle)
- [Fare Calculation](#fare-calculation)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Testing with Postman](#testing-with-postman)
- [Running Tests](#running-tests)
- [Continuous Integration](#continuous-integration)
- [Development Workflow](#development-workflow)

---

## Architecture

```text
                         ┌──────────────────────────┐
         register/login  │   Account Service :8081  │  issues JWT (sub=email, role)
   Client ──────────────▶️│   accountdb (H2)         │
     │                   └──────────────────────────┘
     │  Bearer JWT                     ▲
     │                                 │ shared JWT_SECRET (each service validates locally)
     ▼                                 ▼
┌──────────────────────────┐   GET /api/drivers/available   ┌──────────────────────────┐
│   Ride Service :8083     │───────────────────────────────▶️│  Driver Service :8082    │
│   ridedb (H2)            │                                │  driverdb (H2)           │
│                          │   POST /payments               └──────────────────────────┘
│                          │   PATCH /payments/{id}/status  ┌──────────────────────────┐
│                          │───────────────────────────────▶️│  Payment Service :8084   │
└──────────────────────────┘   X-Internal-Api-Key header    │  faredb (H2)             │
                                                            └──────────────────────────┘
```

**Core rules**

- **Database per service.** No service reads or writes another service's tables directly — only via that service's REST API.
- **Stateless auth.** The Account Service issues JWTs; every other service validates them locally using the shared `JWT_SECRET`. There is no call back to the Account Service per request.
- **Service-to-service calls.** The Ride Service is the orchestrator: it finds an available driver from the Driver Service when a ride is requested, and records the payment with the Payment Service when a ride is completed (authenticated with an internal API key).

---

## Services

| # | Service | Folder | Port | Database | Responsibilities |
|---|---------|--------|------|----------|------------------|
| 1 | Account Service | [`account-service`](account-service) | 8081 | `./data/accountdb` | Registration, login/JWT issuing, profiles, account status and roles |
| 2 | Driver & Vehicle Service | [`driver-service`](driver-service) | 8082 | `./data/driverdb` | Driver registration, vehicles, availability, location, available-driver lookup |
| 3 | Ride Management Service | [`ride-service`](ride-service) | 8083 | `./data/ridedb` | Ride requests, driver assignment, lifecycle transitions, payment on completion |
| 4 | Fare & Payment Service | [`payment-service`](payment-service) | 8084 | `./data/faredb` | Fare estimates, simulated payments, receipts |

Some services have their own detailed README: [driver-service](driver-service/README.md), [ride-service](ride-service/README.md).

---

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, Security) |
| Database | H2 (file-based, persists across restarts) |
| Auth | Spring Security + JWT via JJWT 0.12.6 |
| API docs | springdoc-openapi 3 (Swagger UI) |
| Build | Maven (wrapper included in each service) |
| Testing | JUnit 5, Mockito, Spring Boot test slices |
| CI | GitHub Actions |

---

## Repository Structure

```text
ridelink-backend/
├── account-service/       # Service 1 — accounts & authentication
├── driver-service/        # Service 2 — drivers & vehicles
├── ride-service/          # Service 3 — ride lifecycle (+ its own Postman collection)
├── payment-service/       # Service 4 — fares, payments, receipts
├── docs/
│   └── jwt-contract.md    # Shared JWT contract all services follow
├── postman/
│   ├── RideLink API.postman_collection.json
│   └── RideLink Local.postman_environment.json
└── .github/workflows/
    └── ci.yml             # Builds and tests every service on push/PR
```

Each service follows the same layered layout under `src/main/java/lk/sliit/it3130/<service>/`:

```text
config/       Security, JWT filter, OpenAPI configuration
controller/   REST controllers
service/      Business logic
repository/   Spring Data JPA repositories
model|entity/ JPA entities and enums
dto/          Request/response objects
exception/    Custom exceptions + GlobalExceptionHandler
```

---

## Getting Started

### Prerequisites

- **Java 21** (`java -version` should report 21)
- **Git**
- Maven is optional — every service ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`).

### 1. Clone

```bash
git clone <repository-url>
cd ridelink-backend
git checkout develop
```

### 2. Set the shared secrets

All four services must use the **same** `JWT_SECRET`, and the Ride and Payment services must agree on the internal API key. The real values are shared privately within the team — never commit them.

```bash
# macOS / Linux
export JWT_SECRET="<shared-secret-at-least-32-bytes>"
export INTERNAL_API_KEY="<shared-internal-key>"          # read by payment-service
export PAYMENT_INTERNAL_API_KEY="$INTERNAL_API_KEY"      # read by ride-service
```

```cmd
:: Windows (cmd)
set JWT_SECRET=<shared-secret-at-least-32-bytes>
set INTERNAL_API_KEY=<shared-internal-key>
set PAYMENT_INTERNAL_API_KEY=<shared-internal-key>
```

> If these are not set, every service falls back to the same development-only defaults, so a local setup works out of the box. Don't rely on the defaults outside local development.

### 3. Run the services

Start each service in its own terminal. Recommended order: **Account → Driver → Payment → Ride** (the Ride Service depends on Driver and Payment at runtime).

```bash
cd account-service && ./mvnw spring-boot:run
cd driver-service  && ./mvnw spring-boot:run
cd payment-service && ./mvnw spring-boot:run
cd ride-service    && ./mvnw spring-boot:run
```

On Windows use `mvnw.cmd spring-boot:run`.

### 4. Verify

| Service | Swagger UI |
|---------|-----------|
| Account | http://localhost:8081/swagger-ui/index.html |
| Driver | http://localhost:8082/swagger-ui/index.html |
| Ride | http://localhost:8083/swagger-ui/index.html |
| Payment | http://localhost:8084/swagger-ui/index.html |

### H2 Console

Each service exposes an H2 web console at `http://localhost:<port>/h2-console`.

| Service | JDBC URL | User | Password |
|---------|----------|------|----------|
| Account | `jdbc:h2:file:./data/accountdb` | `sa` | *(empty)* |
| Driver | `jdbc:h2:file:./data/driverdb` | `sa` | *(empty)* |
| Ride | `jdbc:h2:file:./data/ridedb` | `sa` | *(empty)* |
| Payment | `jdbc:h2:file:./data/faredb` | `sa` | *(empty)* |

Database files are created in each service's `data/` folder. Delete that folder to reset a service's data.

---

## Configuration & Environment Variables

| Variable | Used by | Purpose |
|----------|---------|---------|
| `JWT_SECRET` | All services | HMAC key for signing (Account) and validating (others) JWTs. Must be identical everywhere. |
| `INTERNAL_API_KEY` | payment-service | Expected value of the `X-Internal-Api-Key` header on `POST /payments`. |
| `PAYMENT_INTERNAL_API_KEY` | ride-service | Value the Ride Service sends in `X-Internal-Api-Key` when calling the Payment Service. |

Other notable properties (in each `src/main/resources/application.properties`):

| Property | Service | Default |
|----------|---------|---------|
| `ridelink.driver-service.base-url` | ride | `http://localhost:8082` |
| `ridelink.payment-service.base-url` | ride | `http://localhost:8084` |
| `ridelink.http.connect-timeout` | ride | `3s` |
| `ridelink.http.read-timeout` | ride | `5s` |
| `spring.jpa.hibernate.ddl-auto` | all | `update` (schema generated from entities) |

---

## Authentication (JWT)

The full contract is in [`docs/jwt-contract.md`](docs/jwt-contract.md). Summary:

- Issued by the Account Service on `POST /api/auth/login`.
- Signed with HMAC-SHA using the shared `JWT_SECRET` (JJWT `Keys.hmacShaKeyFor()`).
- `sub` claim = the account's **email**.
- `role` claim = `PASSENGER`, `DRIVER` or `ADMIN` (no `ROLE_` prefix; Spring adds it internally).
- Expires **1 hour** after issue.

Send it on protected requests:

```http
Authorization: Bearer <token>
```

Example payload:

```json
{
  "sub": "passenger10@example.com",
  "role": "PASSENGER",
  "iat": 1790053912,
  "exp": 1790057512
}
```

### Roles

| Role | Can do |
|------|--------|
| `PASSENGER` | Request and cancel rides, view rides |
| `DRIVER` | Manage own vehicle, availability and location; accept, start and complete rides; retry payments |
| `ADMIN` | Change account status and roles |

---

## API Reference

🔓 = public · 🔑 = any valid JWT · otherwise the role required is listed.

### Account Service — `:8081`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/accounts/register` | 🔓 | Register a new account |
| POST | `/api/auth/login` | 🔓 | Log in and receive a JWT |
| GET | `/api/accounts/{id}` | 🔑 | Get an account |
| PUT | `/api/accounts/{id}` | 🔑 | Update an account profile |
| PATCH | `/api/accounts/{id}/status` | ADMIN | Set status: `ACTIVE`, `SUSPENDED`, `DEACTIVATED` |
| PATCH | `/api/accounts/{id}/role` | ADMIN | Change role |

### Driver & Vehicle Service — `:8082`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/drivers` | 🔓 | Register a driver |
| POST | `/api/drivers/{id}/vehicle` | DRIVER | Attach a vehicle |
| GET | `/api/drivers/{id}/vehicle` | DRIVER | Get the driver's vehicle |
| PATCH | `/api/drivers/{id}/availability` | DRIVER | Set `AVAILABLE`, `UNAVAILABLE` or `ON_TRIP` |
| PATCH | `/api/drivers/{id}/location` | DRIVER | Update current location |
| GET | `/api/drivers/available` | 🔓 | List available drivers (used by Ride Service) |

### Ride Management Service — `:8083`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/rides` | PASSENGER | Request a ride; an available driver is assigned automatically |
| GET | `/api/rides` | 🔑 | List rides, filter by `passengerId`, `driverId`, `status` |
| GET | `/api/rides/{id}` | 🔑 | Get a ride |
| PATCH | `/api/rides/{id}/accept` | DRIVER | Accept an assigned ride |
| PATCH | `/api/rides/{id}/start` | DRIVER | Start the ride |
| PATCH | `/api/rides/{id}/complete` | DRIVER | Complete the ride and record payment |
| PATCH | `/api/rides/{id}/payment/retry` | DRIVER | Retry a failed payment |
| PATCH | `/api/rides/{id}/cancel` | PASSENGER | Cancel the ride |

Example ride request:

```json
{
  "riderId": 101,
  "pickupLocation": "SLIIT Malabe Campus",
  "destination": "Battaramulla",
  "pickupLatitude": 6.9147,
  "pickupLongitude": 79.9729,
  "destinationLatitude": 6.9022,
  "destinationLongitude": 79.9182,
  "distanceKm": 8.5
}
```

### Fare & Payment Service — `:8084`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/fares/estimate` | 🔓 | Create a fare estimate |
| GET | `/fares/estimate/{id}` | 🔓 | Get a fare estimate |
| POST | `/payments` | Internal API key | Process a payment (called by Ride Service) |
| GET | `/payments/{id}` | 🔑 | Get a payment |
| GET | `/payments/ride/{rideId}` | 🔑 | Get the payment for a ride |
| PATCH | `/payments/{id}/status` | 🔑 | Update payment status |
| GET | `/receipts/{paymentId}` | 🔑 | Get a receipt by payment |
| GET | `/receipts/ride/{rideId}` | 🔑 | Get a receipt by ride |

Example payment request (internal):

```http
POST /payments
X-Internal-Api-Key: <INTERNAL_API_KEY>
Content-Type: application/json
```

```json
{
  "rideId": 1,
  "passengerId": 101,
  "distanceKm": 8.5,
  "durationMin": 20,
  "peakHour": false,
  "paymentMethod": "CARD"
}
```

`paymentMethod` is one of `CASH` (default), `CARD`, `WALLET`.

### Error responses

Every service uses a `GlobalExceptionHandler` that returns a consistent JSON body:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "...",
  "timestamp": "2026-10-10T10:00:00",
  "fieldErrors": { "field": "validation message" }
}
```

Common codes: `400` validation error, `401` missing or invalid token, `403` wrong role, `404` not found, `409` business rule conflict (e.g. invalid ride transition, `NO_AVAILABLE_DRIVER`), `503` downstream service unavailable (e.g. `DRIVER_SERVICE_UNAVAILABLE`).

---

## Ride Lifecycle

```text
REQUESTED ──▶️ ASSIGNED ──▶️ ACCEPTED ──▶️ IN_PROGRESS ──▶️ COMPLETED
    │             │            │
    └─────────────┴────────────┴──────▶️ CANCELLED
```

- `COMPLETED` and `CANCELLED` are terminal.
- `IN_PROGRESS` rides cannot be cancelled.
- Any invalid transition returns **409 Conflict**.

When a ride is completed, the Ride Service calls the Payment Service. The ride keeps its own payment record status:

| Payment record status | Meaning |
|-----------------------|---------|
| `NOT_APPLICABLE` | Ride not completed yet |
| `PENDING` | Completed; payment still to be recorded |
| `RECORDED` | Payment recorded by Payment Service |
| `FAILED` | Recording failed. Use `PATCH /api/rides/{id}/payment/retry` |

---

## Fare Calculation

The Payment Service calculates fares (amounts in LKR):

```text
fare = 100 (base) + 60 × distanceKm + 5 × durationMin
if peakHour: fare × 1.2   (20% surcharge)
```

Payments are **simulated**: a payment succeeds unless the fare exceeds 100,000, in which case it is marked `FAILED`. A receipt is generated for every successful payment. Only one payment is allowed per ride.

---

## API Documentation (Swagger)

Every service exposes interactive OpenAPI docs:

- Swagger UI: `http://localhost:<port>/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:<port>/v3/api-docs`

To call protected endpoints from Swagger, log in through the Account Service, copy the token, click **Authorize** and paste it in.

---

## Testing with Postman

1. Import `postman/RideLink API.postman_collection.json` and `postman/RideLink Local.postman_environment.json`.
2. Select the **RideLink Local** environment.
3. Run the requests in order: register → login (stores the token) → driver setup → ride flow → payment and receipts.

A ride-focused collection is also available at `ride-service/postman/RideLink-Ride-Management.postman_collection.json`.

---

## Running Tests

Run a single service's tests from its folder:

```bash
cd ride-service
./mvnw test          # unit and slice tests
./mvnw verify        # full build, same as CI
```

The suites cover controllers and security rules, exception handling, service logic, and persistence (ride lifecycle rules are covered in depth in `ride-service`).

---

## Continuous Integration

[`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs on every push and pull request to `develop` and `main`. It builds and tests all four services in parallel (`mvn -B verify` on Temurin JDK 21) with `fail-fast: false`, so one failing service doesn't hide the results of the others.

---

## Development Workflow

### Branching

| Branch | Purpose |
|--------|---------|
| `main` | Always demo-ready. Protected; merges only from `develop`. |
| `develop` | Shared integration branch. |
| `feature/<name>` | Individual work, branched off `develop`, merged back through a reviewed PR. |

Documentation-only branches use `docs/<name>`.

### Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/) scoped to the service:

```text
feat(ride-service): add payment retry endpoint
fix(driver-service): reject invalid availability status
docs(payment-service): add tested Postman requests
```

### Ground rules

- Never access another service's database. Add or use a REST endpoint instead.
- Never commit secrets. Use environment variables.
- Any change to the JWT format must be agreed by the whole group and reflected in [`docs/jwt-contract.md`](docs/jwt-contract.md).
- CI must pass before a PR is merged.