# Architecture — ecommerce-monolith

A **modular monolith**: one Spring Boot application built from independent Maven modules, one per DDD bounded context. Clean Architecture inside every module, DDD aggregates + domain events across modules, CQRS for reads vs. writes.

## Module map

| Module        | Bounded context | Aggregate(s)            | Future microservice |
|---------------|-----------------|-------------------------|---------------------|
| shared-kernel | Shared kernel   | — (event contracts, VOs, cross-cutting infra) | becomes `common` |
| catalog       | Product catalog | Product, Category       | catalog-service     |
| inventory     | Stock           | Stock, Reservation      | inventory-service   |
| order         | Ordering        | Order                   | order-service       |
| payment       | Payments        | Payment                 | payment-service     |
| delivery      | Shipping        | Delivery                | delivery-service    |
| app           | Composition root| — (Flyway, datasource, caching, Swagger) | — |

## Rules enforced by the build

1. **No module depends on another module.** Only `shared-kernel` is shared. A compile-time violation of this rule is impossible without changing a pom.
2. **No direct cross-domain persistence access.** Each context owns its write tables and its read-model tables. Other contexts learn facts only via domain events (e.g. the delivery context keeps a local address snapshot from `OrderCreated` instead of querying `orders`).
3. **State-stored, NOT event-sourced.** Aggregates are loaded with `findById()` and persisted with `save()` (JPA INSERT/UPDATE of current state). There is no event store, no replay, no snapshots.
4. **Events are transient.** They travel through the in-process Spring event bus behind the `EventPublisher` interface and are never persisted. `@TransactionalEventListener(AFTER_COMMIT)` drives cross-context saga steps; `@EventListener` drives query projections in the same transaction as the write.

## Persistence model

| Context   | Write tables                                              | Read-model tables          |
|-----------|-----------------------------------------------------------|----------------------------|
| catalog   | products, categories, product_categories                   | product_read_model, category_read_model |
| inventory | inventory, reservations, reservation_items, known_products | inventory_read_model       |
| order     | orders, order_items                                       | order_read_model           |
| payment   | payments                                                  | payment_read_model         |
| delivery  | deliveries, order_address_snapshots                       | delivery_read_model        |

All migrations live in `app/src/main/resources/db/migration/V1__init.sql` (Flyway runs at startup).

## Caching

Spring Cache + Redis (`@EnableCaching`). Every query service method is `@Cacheable` with a 60s TTL — projections update tables via events and the cache expires shortly after. Keys: `product:*`, `products:all`, `inventory:*`, `order:*`, `orders:all`, `payment:*`, `delivery:*`.

## Path to microservices

Each module maps 1:1 to a service in the companion `ecommerce-microservices` project:

| Monolith today                     | Microservices after extraction                              |
|------------------------------------|-------------------------------------------------------------|
| `shared/domain/events/*` (records) | `common` module event contracts                              |
| `EventPublisher` (Spring bus)      | Outbox table + relay → Kafka topics                          |
| `@TransactionalEventListener` saga hooks | `@KafkaListener` consumers (idempotent, deduped by eventId) |
| `@EventListener` projections       | Kafka-fed projections into a per-service MySQL               |
| one MySQL, schema per context      | 5 MySQL instances (one schema per service)                   |
| `app` module jar                   | 5 Spring Boot jars + docker-compose (Kafka + 5× MySQL + Redis)|

The extraction steps are mechanical precisely because:
- module boundaries already forbid cross-context coupling,
- every cross-context reaction is expressed as an event carrying all needed data (no back-calls),
- write and read models are already physically separated (different tables).

## Saga (choreography, no orchestrator)

```
POST /order/create
   └─ Order ctx: persist Order(PENDING) ──emit──▶ OrderCreated
        ├─ Inventory ctx: reserve stock (all-or-nothing)
        │     ├─ ok  ──emit──▶ StockCheckedAndReserved(amount, paymentDetails)
        │     └─ fail──emit──▶ StockCheckFailed(reason)  → Order → REJECTED
        ├─ Payment ctx: charge via mock gateway
        │     ├─ ok  ──emit──▶ PaymentCreated
        │     └─ fail──emit──▶ PaymentFailed  → Order → PAYMENT_FAILED
        │                              └─ Inventory releases the reservation (compensation)
        └─ Delivery ctx: schedule delivery ──emit──▶ DeliveryScheduled → Order → CONFIRMED
```

See `docs/diagrams/order-saga-sequence.md` for the full sequence diagram.
