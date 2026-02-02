# Alt - Microservices Repository

Multi-module Quarkus project with Java 21 and Maven, simulating four microservices.

## Microservices

| Service | Port | Description |
|---------|------|-------------|
| bff-service | 8080 | Backend for Frontend |
| integrations-service | 8081 | External integrations |
| card-service | 8082 | Card service |
| account-service | 8083 | Account service |

## Prerequisites

- Java 21
- Maven 3.8+

## How to run

### Build the entire project
```bash
mvn clean install
```

### Run a specific microservice
```bash
# Development mode (with hot-reload)
mvn -pl bff-service quarkus:dev
mvn -pl integrations-service quarkus:dev
mvn -pl card-service quarkus:dev
mvn -pl account-service quarkus:dev

# Production mode
mvn -pl bff-service quarkus:run
mvn -pl integrations-service quarkus:run
mvn -pl card-service quarkus:run
mvn -pl account-service quarkus:run
```

### Swagger Documentation (BFF Service)

With bff-service running, OpenAPI documentation is available at:

- **Swagger UI:** http://localhost:8080/q/swagger-ui
- **OpenAPI JSON:** http://localhost:8080/q/openapi

### Test the services

Each service exposes a health endpoint at `/api/health`:

- http://localhost:8080/api/health (bff-service)
- http://localhost:8081/api/health (integrations-service)
- http://localhost:8082/api/health (card-service)
- http://localhost:8083/api/health (account-service)

SmallRye Health is also available at `/q/health` on each service.
