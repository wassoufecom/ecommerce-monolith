# ecommerce-monolith

E-commerce backend built as a **modular monolith**: Clean Architecture + DDD + CQRS (state-stored, transient in-process events). One Maven module per bounded context — the stepping stone to microservices.

## Stack

| Concern    | Technology |
|------------|------------|
| Language   | Java 21 (Temurin) |
| Framework  | Spring Boot 3.2.5 |
| Database   | MySQL 8.0 (Docker) |
| Cache      | Redis 7 (Docker) — cache-aside on all query endpoints (60s TTL) |
| Migrations | Flyway |
| API docs   | springdoc-openapi (Swagger UI: `http://localhost:8080/swagger-ui/index.html`) |
| Frontend   | React 18 + Vite + Material UI (reusable components + hooks) |
| Build      | Maven (multi-module) |

## Modules (bounded contexts)

```
ecommerce-monolith/
├── shared-kernel/   # DomainEvent, EventPublisher, VOs, event contracts, cross-cutting infra
├── order/           # Order aggregate + CQRS     (→ future order-service)
├── inventory/       # Stock + reservations       (→ future inventory-service)
├── payment/         # Payments + mock gateway    (→ future payment-service)
├── delivery/        # Deliveries                 (→ future delivery-service)
├── catalog/         # Products + categories      (→ future catalog-service)
└── app/             # Boot module — assembles everything into one jar
```

**No module depends on another module.** Cross-context communication happens exclusively through domain events published via the shared `EventPublisher`. That is exactly what makes each module a drop-in candidate for extraction into a microservice (see `docs/ARCHITECTURE.md`, "Path to microservices").

## Run

```bash
# 1. Infrastructure
docker compose up -d            # MySQL 8.0 + Redis 7

# 2. Build & run the backend
mvn clean package -DskipTests
java -jar app/target/ecommerce-monolith-1.0.0.jar

# 3. Frontend (optional)
cd frontend
npm install
npm run dev                     # http://localhost:5173 (proxies API to :8080)
```

## Try the saga

```bash
# Create a category + product
curl -X POST localhost:8080/category/create -H 'Content-Type: application/json' -d '{"name":"computers"}'
curl -X POST localhost:8080/product/create  -H 'Content-Type: application/json' \
  -d '{"title":"Laptop","description":"16GB RAM","price":999.99,"categories":["computers"]}'

# Add stock (use the productId from the previous response)
curl -X POST localhost:8080/inventory/add -H 'Content-Type: application/json' \
  -d '{"productId":"<PRODUCT_ID>","quantity":10}'

# Place an order — the choreographed saga runs automatically:
# OrderCreated → StockCheckedAndReserved → PaymentCreated → DeliveryScheduled
curl -X POST localhost:8080/order/create -H 'Content-Type: application/json' -d '{
  "userId":"customer-1",
  "address":{"street":"Main St 1","city":"Warsaw","zip":"00-001","country":"PL"},
  "orderItems":[{"productId":"<PRODUCT_ID>","quantity":1,"price":999.99}],
  "price":999.99,
  "paymentDetails":{"cardNumber":"4111111111111111","cvv":"123","expiryDate":"12/28"}
}'

# Watch the query side converge (Redis-cached reads)
curl localhost:8080/order/<ORDER_ID>
curl localhost:8080/payment/order/<ORDER_ID>
curl localhost:8080/delivery/order/<ORDER_ID>
```

Failure paths:
- **Insufficient stock** → order status `REJECTED`, no payment attempted.
- **Declined card** (`4000000000000002`) → order status `PAYMENT_FAILED`, reserved stock is released (compensation).

## Docs

- `docs/ARCHITECTURE.md` — structure, module boundaries, path to microservices
- `docs/CQRS.md` — command/query separation and projections
- `docs/BUSINESS_LOGIC.md` — canonical business logic
- `docs/diagrams/` — Mermaid component, layer, saga sequence, deployment, ER diagrams
