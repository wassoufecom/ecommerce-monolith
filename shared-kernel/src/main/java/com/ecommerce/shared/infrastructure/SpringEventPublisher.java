package com.ecommerce.shared.infrastructure;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Transports domain events through the in-process Spring event bus.
 * Events are transient notifications — they are never persisted.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event [eventId={}, type={}]",
                event.eventId(), event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
