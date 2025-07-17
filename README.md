# Veritas Bank Microservices Platform

## 1. Introduction

Veritas Bank is a modern, cloud-native banking platform built using a microservices architecture. The application leverages the Netflix OSS stack to provide scalable, resilient, and discoverable services. Core features include user management, transaction processing, and real-time notifications, all accessible through a unified API gateway and a responsive web interface.

**Key Features:**
- User registration, authentication, and profile management
- Secure money transfers and transaction history
- Real-time notifications for account activity
- Service discovery, load balancing, and fault tolerance
- Centralized API gateway for all client requests

## 2. Architecture

### 2.1 Architectural Diagram


### 2.2 Design Decisions
- **Service Decomposition:**
  - **User Service:** Handles authentication, registration, and user profiles. Decoupled for independent scaling and security.
  - **Transaction Service:** Manages all financial operations, ensuring transactional integrity and auditability.
  - **Notification Service:** Sends real-time alerts via email/SMS, decoupled for asynchronous processing.
- **Netflix OSS Stack:**
  - **Eureka** for service discovery and health monitoring.
  - **Zuul** as a single entry point and API gateway.
  - **Ribbon** for client-side load balancing.
  - **Hystrix** for circuit breaking and resilience.
  - **Spring Boot** for rapid service development.

## 3. Microservices

### 3.1 Implementation Stack
- **Spring Boot**: Rapid development of RESTful services
- **Eureka**: Service registry and discovery
- **Zuul**: API gateway and dynamic routing
- **Ribbon**: Load balancing between service instances
- **Hystrix**: Circuit breaker for fault tolerance

### 3.2 Core Services

#### 3.2.1 User Service
- **Functionality:** User registration, login, profile management, and authentication.
- **Endpoints:**
  - `POST /users/register` — Register a new user
  - `POST /users/login` — Authenticate and issue JWT
  - `GET /users/{id}` — Get user profile
  - `PUT /users/{id}` — Update user profile
- **Inter-service:**
  - Issues JWT tokens for secure communication
  - Registers with Eureka for discovery

#### 3.2.2 Transaction Service
- **Functionality:** Handles all account transactions, transfers, and transaction history.
- **Endpoints:**
  - `POST /transactions/transfer` — Transfer funds between accounts
  - `GET /transactions/{userId}` — List all transactions for a user
  - `GET /transactions/{id}` — Get transaction details
  - `POST /transactions/deposit` — Deposit funds
  - `POST /transactions/withdraw` — Withdraw funds
- **Inter-service:**
  - Notifies Notification Service on transaction events
  - Uses Ribbon for load-balanced calls to Notification Service
  - Registers with Eureka

#### 3.2.3 Notification Service
- **Functionality:** Sends notifications (email/SMS) for account activity and alerts.
- **Endpoints:**
  - `POST /notifications/send` — Send a notification
  - `GET /notifications/{userId}` — List notifications for a user
  - `POST /notifications/subscribe` — Subscribe to notification types
  - `DELETE /notifications/unsubscribe` — Unsubscribe from notifications
- **Inter-service:**
  - Consumes messages from Transaction Service (via REST or message broker)
  - Registers with Eureka

### 3.3 Discovery Server (Eureka)
- All services register with Eureka at startup.
- Eureka dashboard provides health status and instance monitoring.
- Enables dynamic scaling and failover.

### 3.4 API Gateway (Zuul)
- **Configuration:**
  - Routes all `/api/*` traffic to appropriate services
  - Handles authentication, rate limiting, and CORS
  - Uses Eureka for dynamic routing
- **Role:**
  - Single entry point for all clients
  - Centralized logging and security

## 4. User Interface

### 4.1 Implementation
- **Framework:** React.js (with Redux or Context API for state management)
- **API Integration:** Axios or Fetch for REST calls via Zuul
- **Features:**
  - User registration/login forms
  - Dashboard for account overview and transactions
  - Real-time notification panel

### 4.2 API Testing
- **Postman:** Used for manual API testing and collection sharing
- **Swagger/OpenAPI:** Auto-generated docs for each service (optional)

## 5. Deployment

### 5.1 Local Deployment
- Prerequisites: Java 17+, Docker, Maven
- Steps:
  1. Clone the repository
  2. Start Eureka server: `cd discovery-server && mvn spring-boot:run`
  3. Start Zuul gateway: `cd api-gateway && mvn spring-boot:run`
  4. Start each microservice: `cd user-service && mvn spring-boot:run` (repeat for others)
  5. Start frontend: `cd frontend && npm install && npm start`

### 5.2 Docker Deployment
- Each service includes a `Dockerfile`
- Use `docker-compose` for orchestration:
  ```yaml
  version: '3.8'
  services:
    discovery-server:
      build: ./discovery-server
      ports:
        - "8761:8761"
    api-gateway:
      build: ./api-gateway
      ports:
        - "8080:8080"
      depends_on:
        - discovery-server
    user-service:
      build: ./user-service
      ports:
        - "8081:8081"
      depends_on:
        - discovery-server
    transaction-service:
      build: ./transaction-service
      ports:
        - "8082:8082"
      depends_on:
        - discovery-server
    notification-service:
      build: ./notification-service
      ports:
        - "8083:8083"
      depends_on:
        - discovery-server
    frontend:
      build: ./frontend
      ports:
        - "3000:3000"
      depends_on:
        - api-gateway
  ```
- Start all: `docker-compose up --build`

### 5.3 Cloud Deployment
- Deploy to AWS ECS, Azure AKS, or Google GKE
- Use managed databases and message brokers
- Configure environment variables and secrets

## 6. Source Code

- **GitHub Repository:** [https://github.com/your-org/veritas-bank-microservices](https://github.com/your-org/veritas-bank-microservices)

### 6.1 Development Challenges & Solutions
- **Service Communication:** Used Eureka and Ribbon for dynamic service discovery and load balancing.
- **Fault Tolerance:** Integrated Hystrix for circuit breaking and fallback logic.
- **API Security:** JWT-based authentication at the gateway and service level.
- **Data Consistency:** Used distributed transactions and message queues for eventual consistency.
- **Testing:** Employed Postman and Swagger for API validation and contract testing.

## 7. References
- [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Netflix Zuul](https://github.com/Netflix/zuul)
- [Microservices application](https://github.com/zoltanvin/royal-reserve-bank.git)
- [Netflix Ribbon](https://github.com/Netflix/ribbon)
- [Netflix Hystrix](https://github.com/Netflix/Hystrix)
- [Docker Documentation](https://docs.docker.com/)
- [React Documentation](https://react.dev/)
- [Postman](https://www.postman.com/)
