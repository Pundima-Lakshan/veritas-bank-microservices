## Veritas Bank Microservices

### Project Description
A microservices-based simple banking application.

**Tech Stack:**
- **Backend:** Java 17, Spring Boot, Spring Cloud (Eureka, Config Server, Gateway), MongoDB, PostgreSQL, MySQL, Kafka
- **Infrastructure:** Docker, Docker Compose, Prometheus, Grafana, Zipkin
- **Frontend:** Minimal web UI for checking real-time notifications (WebSocket-based)

This project demonstrates core banking features (accounts, transactions, asset management, notifications) using a microservices architecture. The frontend is intentionally simple, focused on displaying real-time notifications to users.


Key highlights:
- **Microservices Architecture**: Each domain (account, transaction, asset, notification) is an independent service.
- **API Gateway**: Centralized entry point for routing, authentication, and security.
- **Service Discovery**: Dynamic service registration and discovery using Eureka.
- **Centralized Configuration**: Managed via Spring Cloud Config Server.
- **Resilience**: Circuit breaker, retry, and timeout patterns for robust operations.
- **Real-time Notifications**: WebSocket-based notification system for instant updates.
- **Polyglot Persistence**: Uses MongoDB, PostgreSQL, and MySQL for different services.
- **Monitoring & Tracing**: Integrated with Prometheus, Grafana, and Zipkin.

---

### Getting Started (Development - BE)

#### Prerequisites
- Java 17+
- Maven
- Docker & Docker Compose
- (Recommended) IntelliJ IDEA or Eclipse

#### 1. Clone the Repository
```bash
git clone <your-fork-or-this-repo-url>
cd veritas-bank-microservices/be
```

#### 2. Start Infrastructure Services
Start supporting services (databases, Kafka, Prometheus, Grafana, etc.) using Docker Compose:
```bash
docker-compose -f docker-compose-infrastructure-services.yml up -d
```

#### 3. Build All Applications
From the project root:
```bash
mvn clean install -pl !config-server
```
Or build a specific service:
```bash
mvn clean install -pl <module-name>
# Example: mvn clean install -pl account-api
```

#### 4. Run Microservices
Start each service (account-api, transaction-api, asset-management-api, notification-api, api-gateway, discovery-server) from your IDE or using Maven:
```bash
cd <service-folder>
mvn spring-boot:run
```

#### 8. Authentication
All endpoints require Bearer token authentication via Auth0. Configure your Auth0 credentials in the config-server.

---

## Veritas Bank Microservices - Complete Endpoint Documentation

### 1. Account API (`/api/account`)
**Base URL**: `http://localhost:8080/api/account` (via API Gateway)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/` | Create a new bank account | `AccountRequest` | Success message (201) |
| `GET` | `/` | Get all accounts for authenticated user | - | `List<AccountResponse>` (200) |
| `DELETE` | `/` | Delete account by account holder name | `AccountRequest` | Success message (200) or error (404) |
| `DELETE` | `/{id}` | Delete account by ID | - | Success message (200) or error (404) |
| `GET` | `/{id}` | Get account by ID | - | `AccountResponse` (200) or 404 |
| `POST` | `/{id}/debit` | Debit account by amount | `DebitCreditRequest` | Success message (200) or error (400) |
| `POST` | `/{id}/credit` | Credit account by amount | `DebitCreditRequest` | Success message (200) |

### 2. Transaction API (`/api/transaction`)
**Base URL**: `http://localhost:8080/api/transaction` (via API Gateway)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/` | Process transaction asynchronously | `TransactionRequest` | `CompletableFuture<String>` (201) |
| `GET` | `/` | Get all transactions for authenticated user | - | `List<Transaction>` (200) |

### 3. Asset Management API (`/api/asset-management`)
**Base URL**: `http://localhost:8080/api/asset-management` (via API Gateway)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/` | Check asset availability | Query params: `assetCode`, `amount` | `List<AssetManagementResponse>` (200) |
| `POST` | `/update-amount` | Update asset amount | Query params: `assetCode`, `amount` | Success message (200) |
| `POST` | `/` | Create new asset | `Asset` | `Asset` (201) |
| `GET` | `/{id}` | Get asset by ID | - | `Asset` (200) |
| `GET` | `/all` | Get all assets | - | `List<Asset>` (200) |
| `PUT` | `/{id}` | Update asset by ID | `Asset` | `Asset` (200) |
| `DELETE` | `/{id}` | Delete asset by ID | - | No content (204) |

### 4. Notification API (WebSocket)
**WebSocket Endpoint**: `ws://localhost:8080/ws-notifications` (via API Gateway)

| Type | Endpoint | Description |
|------|----------|-------------|
| WebSocket | `/ws-notifications` | WebSocket endpoint for real-time notifications |
| STOMP Topic | `/topic` | Message broker for broadcasting notifications |
| STOMP App | `/app` | Application destination prefix for sending messages |

### 5. Infrastructure Services (via API Gateway)

| Service | Endpoint | Description |
|---------|----------|-------------|
| Discovery Server | `/discovery-server` | Eureka discovery server UI |
| Discovery Server | `/eureka/**` | Eureka static resources |
| Config Server | `/config-server/**` | Configuration server |
| API Gateway | `/` | API Gateway root |

### Authentication & Security
- All endpoints require Bearer token authentication via Auth0
- JWT tokens are validated and user ID is extracted from the `sub` claim
- CORS is configured for `http://localhost:5173` (frontend)

### Circuit Breaker & Resilience
- Transaction API implements circuit breaker pattern with fallback methods
- Async processing with `CompletableFuture` for transaction operations
- Retry and timeout configurations are applied

### Key Features
1. **Microservices Architecture**: Each service is independently deployable
2. **API Gateway**: Centralized routing and authentication
3. **Service Discovery**: Eureka-based service registration
4. **Configuration Management**: Centralized config via Config Server
5. **Real-time Notifications**: WebSocket-based notification system
6. **Resilience Patterns**: Circuit breaker, retry, and timeout mechanisms

---

### Credits
This project is based on Royal Reserve Bank project, available at https://github.com/zoltanvin/royal-reserve-bank.