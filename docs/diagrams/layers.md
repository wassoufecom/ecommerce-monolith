# Layer diagram — Clean Architecture inside every module

Each bounded-context module follows the same dependency rule: **outer layers know inner layers, never the reverse.** The core (`domain`) has zero Spring/JPA imports.

```mermaid
graph TB
    subgraph Presenters["presenters (outermost — Spring aware)"]
        REST["rest controllers<br/>POST /order/create, GET /order/{id}"]
        HANDLERS["handlers<br/>@EventListener projections<br/>@TransactionalEventListener saga steps"]
    end

    subgraph CQRS["commands / queries (CQRS entry points)"]
        CMD["commands<br/>CreateOrderCommand + handler<br/>(Bean Validation)"]
        QRY["queries<br/>OrderQueryService + read model<br/>(@Cacheable → Redis)"]
    end

    subgraph Data["data (adapters)"]
        JPA["jpa entities<br/>OrderJpaEntity (current state)"]
        SPRINGDATA["Spring Data repositories"]
        MAP["mappers<br/>aggregate ⇄ entity"]
        IMPL["implementations<br/>OrderRepositoryImpl"]
    end

    subgraph Core["core (innermost — pure domain)"]
        AGG["domain<br/>Order aggregate + OrderStatus<br/>invariants enforced here"]
        REPO["repositories<br/>OrderRepository (interface)"]
        USE["usecases<br/>CreateOrderUseCase,<br/>UpdateOrderStatusUseCase"]
    end

    REST --> CMD --> USE
    HANDLERS --> USE
    QRY -.->|"reads"| RMODEL[("order_read_model")]
    USE --> AGG
    USE --> REPO
    IMPL -.->|implements| REPO
    IMPL --> MAP
    IMPL --> SPRINGDATA --> JPA
```

Dependency direction: `presenters → commands/queries → core` and `data → core`. The core defines repository **interfaces**; `data` provides the JPA implementation (dependency inversion).

| Layer          | May depend on                      | Must NOT depend on            |
|----------------|------------------------------------|-------------------------------|
| core/domain    | shared-kernel value objects        | Spring, JPA, other modules    |
| core/usecases  | core/domain, repository interfaces | JPA, other modules            |
| data           | core (implements interfaces)       | other modules                 |
| presenters     | commands/usecases/queries          | other modules (events only via shared-kernel) |
