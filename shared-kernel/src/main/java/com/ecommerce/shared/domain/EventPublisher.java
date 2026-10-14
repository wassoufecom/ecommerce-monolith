package com.ecommerce.shared.domain;

/**
 * Abstraction over the event transport so the core never depends on Spring.
 * Implementation: in-process Spring ApplicationEventPublisher.
 */
public interface EventPublisher {

    void publish(DomainEvent event);
}
