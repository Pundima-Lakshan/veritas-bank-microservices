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
