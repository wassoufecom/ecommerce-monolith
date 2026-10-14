# Deployment diagram — ecommerce-monolith

```mermaid
graph TB
    subgraph Dev["Developer machine"]
        subgraph Docker["Docker Desktop"]
            MYSQL[("mysql:8.0<br/>container: ecommerce-monolith-mysql<br/>port 3306, db: ecommerce")]
            REDIS[("redis:7-alpine<br/>container: ecommerce-monolith-redis<br/>port 6380")]
        end

        subgraph App["Spring Boot app (jar or container)"]
            JAR["java -jar app/target/ecommerce-monolith-1.0.0.jar<br/>maven build: maven:3.9-eclipse-temurin-21<br/>runtime: eclipse-temurin:21-jre<br/>port 8080"]
        end

        subgraph FE["Frontend (Vite dev server or nginx)"]
            REACT["React SPA — localhost:5173<br/>(dev proxy → :8080)"]
        end

        BROWSER["Browser"]
    end

    BROWSER --> REACT
    REACT -->|"HTTP /product, /order, /inventory, /payment, /delivery"| JAR
    JAR -->|"JDBC (Flyway migrations at startup)"| MYSQL
    JAR -->|"Spring Data Redis (cache-aside, 60s TTL)"| REDIS
```

Compose services: `mysql` (healthcheck `mysqladmin ping`), `redis` (healthcheck `redis-cli ping`). The app can run on the host (default) or as a container built from the multi-stage Dockerfile.
