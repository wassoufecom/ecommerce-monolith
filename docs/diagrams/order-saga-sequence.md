# Order saga sequence — choreographed, in-process events

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant O as Order context
    participant Bus as Spring event bus<br/>(transient, in-process)
    participant I as Inventory context
    participant P as Payment context
    participant D as Delivery context

    C->>O: POST /order/create (userId, items, price, address, card)
    Note over O: validate: >=1 item, price>0,<br/>address complete, total = item sum
    O->>O: OrderRepository.save(order PENDING)
    O-->>Bus: OrderCreated (transient)
    Note over Bus: @EventListener projections<br/>update order_read_model in-tx
    O-->>C: 201 { orderId }

    Bus--)I: OrderCreated (AFTER_COMMIT)
    Note over I: all-or-nothing stock check
    alt stock available
        I->>I: deduct stock, save reservation
        I-->>Bus: StockCheckedAndReserved (amount, card)
        Note over I: also StockReserved → projection
    else insufficient stock
        I-->>Bus: StockCheckFailed (reason)
        Bus--)O: StockCheckFailed → order REJECTED
    end

    Bus--)P: StockCheckedAndReserved (AFTER_COMMIT)
    Note over P: payment for order must be first,<br/>amount = order total, card valid?
    P->>P: Payment(PENDING) → MockGateway.charge()
    alt charge approved
        P->>P: Payment(COMPLETED, token, last4)
        P-->>Bus: PaymentCreated
    else card declined (4000000000000002)
        P->>P: Payment(FAILED, reason)
        P-->>Bus: PaymentFailed
        Bus--)O: PaymentFailed → order PAYMENT_FAILED
        Bus--)I: PaymentFailed → release reservation (compensation)
    end

    Bus--)D: PaymentCreated (AFTER_COMMIT)
    Note over D: address already snapshotted from OrderCreated
    D->>D: Delivery(SCHEDULED) — one per order
    D-->>Bus: DeliveryScheduled
    Bus--)O: DeliveryScheduled → order CONFIRMED

    C->>O: GET /order/{id} (query side, Redis-cached)
    O-->>C: status CONFIRMED
```

Failure handling (BUSINESS_LOGIC.md §10):
- stock fails → `REJECTED`, no payment attempted
- payment fails → `PAYMENT_FAILED` + stock released (compensation)
- delivery fails → `DELIVERY_FAILED` (payment refund: future enhancement)
