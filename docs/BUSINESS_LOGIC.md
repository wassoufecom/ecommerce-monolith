BUSINESS\_LOGIC.md

This document describes the business logic for the E-Commerce platform. It applies to both the monolithic (Clean Architecture) and microservices (Event Sourcing + CQRS) implementations. The same rules, invariants, and workflows must hold in both projects — only the technical mechanism differs.



1\. Purpose

The platform is a backend e-commerce system that supports:



Browsing and managing a product catalog



Tracking inventory stock



Placing customer orders



Processing payments



Scheduling deliveries



The core business process is order fulfillment, coordinated across five domains: Catalog, Inventory, Order, Payment, and Delivery.



2\. Domains and Responsibilities

Domain	Owns	Responsibility

Catalog	Products, Categories	Manage the sellable product catalog

Inventory	Stock levels per product	Track and reserve stock

Order	Orders, order items	Capture customer intent to buy and orchestrate fulfillment

Payment	Payments	Charge the customer for an order

Delivery	Deliveries	Schedule and track shipment to the customer

Each domain is independent. Domains must not call each other directly — they communicate only through domain events.



3\. Ubiquitous Language

Term	Meaning

Product	A sellable item defined in the Catalog with title, description, price, and categories

Category	A grouping of products (e.g., "computers")

Stock	The quantity of a product available for sale

Reservation	A temporary hold on stock while an order is being processed

Order	A customer's request to purchase one or more products

Order Item	A single product and quantity within an order

Payment	A financial transaction for an order

Delivery	A shipment of an order to a shipping address

Saga	The end-to-end order fulfillment process coordinated by events

4\. Domain Rules and Invariants

4.1 Catalog

A Product must have a non-empty title and a positive price.



A Product may belong to zero or more categories.



A Category must have a unique, non-empty name.



Product IDs are unique across the catalog.



Prices are always expressed in a single currency (system-wide).



4.2 Inventory

Stock quantity for a product is always >= 0.



Stock can be added only with a positive amount.



Stock can be reserved only if availableStock >= requestedQuantity.



A reservation reduces available stock immediately.



If reservation fails, the order cannot proceed.



Reserved stock is released if the order is cancelled (future enhancement).



4.3 Order

An Order must contain at least one order item.



Every order item must have a quantity >= 1.



Every order must have a positive total price.



Every order must have a valid shipping address (street, city, ZIP, country).



Every order must have a userId identifying the customer.



The order total price must match the sum of item.price \* quantity (validated against catalog).



Once created, an order's items and address are immutable (a new order must be created to change them).



4.4 Payment

A payment can only be created for an order that has successfully reserved stock.



A payment must reference exactly one order.



The payment amount must equal the order total.



Card details (card number, CVV, expiration date) must be provided and valid.



A payment has one of these statuses: PENDING, COMPLETED, FAILED.



A failed payment blocks delivery scheduling.



4.5 Delivery

A delivery can only be scheduled for an order whose payment is COMPLETED.



A delivery must reference a shipping address obtained from the order.



A delivery has one of these statuses: SCHEDULED, IN\_TRANSIT, DELIVERED, CANCELLED.



Only one active delivery exists per order.



5\. Core Business Workflow: Order Fulfillment

The order fulfillment process is a choreographed saga. Each step is triggered by an event from the previous step. No central orchestrator exists.



Step-by-Step

Step 1 — Order Creation (Order domain)



Trigger: POST /order/create with userId, address, orderItems\[], price, paymentDetails.



Rules applied:



Order must have at least one item.



Price must be positive.



Address must be complete.



Action: create and persist the Order aggregate.



Event emitted: OrderCreated (carries orderId, userId, items, price, address).



Step 2 — Inventory Validation (Inventory domain)



Trigger: OrderCreated.



Rules applied:



For each item, availableStock >= requestedQuantity.



If any item fails, the whole order fails.



Action (on success):



Deduct stock for each item.



Persist reservation.



Event emitted: StockCheckedAndReserved.



Event emitted (on failure): StockCheckFailed (future enhancement: compensation).



Step 3 — Payment Processing (Payment domain)



Trigger: StockCheckedAndReserved.



Rules applied:



Order total must equal the requested charge amount.



Card details must be valid.



Action:



Register the payment with status PENDING.



Attempt to charge.



On success, mark COMPLETED.



Event emitted: PaymentCreated.



Event emitted (on failure): PaymentFailed.



Step 4 — Delivery Scheduling (Delivery domain)



Trigger: PaymentCreated.



Rules applied:



Payment must be COMPLETED.



Address must be present.



Action:



Retrieve shipping address from the order.



Persist the Delivery aggregate with status SCHEDULED.



Event emitted: DeliveryScheduled.



Failure Handling

If stock reservation fails → the order is marked REJECTED. No payment is attempted.



If payment fails → the order is marked PAYMENT\_FAILED. Reserved stock is released (compensation).



If delivery scheduling fails → the order is marked DELIVERY\_FAILED. Payment is refunded (future enhancement).



6\. Use Cases

Catalog

CreateProduct: add a new product with title, description, price, categories.



CreateCategory: add a new category.



ListProducts: retrieve products (query side).



Inventory

AddStock: increase available stock for a product.



ReserveStock: reserve stock for an order's items. Triggered by OrderCreated.



ReleaseStock: release reserved stock (compensation, future).



Order

CreateOrder: create a new order and emit OrderCreated.



GetOrder: retrieve an order by ID (query side).



Payment

ProcessPayment: charge the customer. Triggered by StockCheckedAndReserved.



GetPayment: retrieve a payment by order ID (query side).



Delivery

ScheduleDelivery: schedule a delivery. Triggered by PaymentCreated.



GetDelivery: retrieve a delivery by order ID (query side).



7\. Events

Event	Producer	Consumers	Payload

OrderCreated	Order	Inventory	orderId, userId, items, price, address

StockCheckedAndReserved	Inventory	Payment	orderId

StockCheckFailed	Inventory	Order	orderId, reason

PaymentCreated	Payment	Delivery	orderId

PaymentFailed	Payment	Order, Inventory	orderId, reason

DeliveryScheduled	Delivery	Order	orderId, address

Event contract rules:



Events are immutable facts — they describe something that already happened.



Events are named in the past tense.



Events carry all data consumers need (no back-calls to the producer).



Event schemas are versioned; consumers must tolerate unknown fields.



8\. Business Rules by Endpoint

Endpoint	Domain	Rule Enforced

POST /product/create	Catalog	Title non-empty, price > 0, categories optional

POST /category/create	Catalog	Name unique and non-empty

POST /inventory/add	Inventory	Product exists, quantity > 0

POST /order/create	Order	≥1 item, price > 0, address complete

9\. Consistency Rules

Strong consistency within a single domain (one aggregate, one transaction).



Eventual consistency across domains — an order becomes fully fulfilled only after all saga steps succeed.



Each domain commits its own state and publishes events atomically using the Outbox pattern (microservices) or Spring events within a transaction (monolith).



A domain must never query another domain's database directly. It relies on events.



10\. Compensating Actions (Failure Recovery)

Failure	Compensation

Stock reservation fails	Order marked REJECTED; no payment attempted

Payment fails	Order marked PAYMENT\_FAILED; stock reservation released

Delivery fails	Order marked DELIVERY\_FAILED; payment refunded (future)

Compensations are triggered by failure events and are the responsibility of the originating domain.



11\. Non-Functional Business Constraints

Idempotency: event handlers must be idempotent — the same event delivered twice must not double-reserve stock or double-charge.



Auditability: every state change must be traceable to an event (fully satisfied by Event Sourcing in the microservices project).



Ordering: events for the same orderId must be processed in order (Kafka partition key = orderId).



Data privacy: card details must never be persisted in plain text; only a token or last-4 digits are stored.



Currency: all prices use a single system-wide currency (no multi-currency support at this time).



12\. Explicitly Out of Scope

User authentication and authorization



Refunds and returns



Multi-currency pricing



Discount codes and promotions



Partial shipments



Backorders



Real payment gateway integration (mock implementation only)



13\. How the Two Projects Implement This Logic

Business Concern	Monolith	Microservices

Domain boundaries	Packages per domain	Separate Spring Boot services

Event delivery	Spring ApplicationEventPublisher (in-process)	Kafka topics

Event atomicity	JPA transaction + @TransactionalEventListener	Outbox table + relay poller

Read model	JPA repositories	Dedicated projection tables updated by Kafka consumers

Failure handling	Exceptions + compensating event handlers	Failure events + compensating handlers

Audit	Order/event log tables	Event store (append-only)

Both projects must obey every rule in sections 4–11. The only difference is the technical transport.



14\. Summary

The business logic is a choreographed event-driven order fulfillment saga built on five independent domains. Order creation triggers a chain: inventory reservation → payment → delivery. Each domain enforces its own invariants, publishes events, and reacts to events from others. Consistency is strong within a domain and eventual across domains. All state changes are driven by domain events, which makes the logic testable, traceable, and portable between the monolithic and microservices implementations.

