# CQRS — ecommerce-monolith

Command Query Responsibility Segregation inside one Spring Boot app: separate packages, separate models, separate tables — same MySQL instance.

## Command side (write)

- **Entry:** `commands/` package per module (`CreateOrderCommand` + `CreateOrderCommandHandler`, `AddStockCommand` + handler, ...).
- **Flow:** command DTO (Bean Validation) → command handler → use case (`core/usecases`) → aggregate (`core/domain`, invariants enforced here) → repository interface → JPA implementation (`data/`) → **current-state** table.
- **No event sourcing:** writes are plain INSERT/UPDATE of aggregate state (`repository.findById()` + `save()`).
- After the write transaction commits, the domain event triggers the next saga step in another context (`@TransactionalEventListener(AFTER_COMMIT)`).

## Query side (read)

- **Entry:** `queries/` package per module (`OrderQueryService`, `InventoryQueryService`, `ProductQueryService`, `PaymentQueryService`, `DeliveryQueryService`).
- **Flow:** GET controller → query service (`@Transactional(readOnly=true)`) → read-model table → immutable view record (DTO). `@Cacheable` stores results in Redis (60s TTL).
- **Projections:** `presenters/handlers/*ProjectionUpdater` classes subscribe with `@EventListener` to the same transient domain events used by the command side and upsert the read-model tables **inside the producer's transaction** — write and projection commit atomically.

## Read-model tables

| Module    | Read table              | Updated by                                              |
|-----------|-------------------------|---------------------------------------------------------|
| catalog   | product_read_model      | ProductCreated                                          |
| catalog   | category_read_model     | CategoryCreated                                         |
| inventory | inventory_read_model    | StockAdded, StockReserved, StockReleased                |
| order     | order_read_model        | OrderCreated + all saga status events                   |
| payment   | payment_read_model      | PaymentCreated, PaymentFailed                           |
| delivery  | delivery_read_model     | DeliveryScheduled                                       |

## Why two paths?

1. **Reads never execute business rules** — no validation, no aggregates loaded, just flat-table reads (optionally cached).
2. **The read model can be denormalized freely** (JSON item arrays, comma-separated categories, pre-joined statuses) without touching domain logic.
3. **It mirrors the microservices design**: in the companion project the exact same read models are updated by Kafka listeners instead of in-process events — the only difference is the transport.

## Event flow (in-process, transient)

```
Command handler (@Transactional)
   └─ aggregate mutated, state saved
       └─ EventPublisher.publish(event)          ← same transaction
           ├─ @EventListener projections          ← run in-tx (atomic with the write)
           └─ ... transaction commits ...
               └─ @TransactionalEventListener(AFTER_COMMIT)  ← next saga step, new transaction
```

Events are never persisted. If the JVM dies between commit and after-commit processing, the saga step is lost — accepted trade-off in the monolith (documented in BUSINESS_LOGIC.md §11); the microservices project solves it with the durable outbox + Kafka.
