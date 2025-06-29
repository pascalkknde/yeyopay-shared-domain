package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import reactor.core.publisher.Mono;

/**
 * Abstract base class for event handlers with common functionality.
 */
public abstract class BaseEventHandler<T extends DomainEvent> implements EventHandler<T> {
    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @Override
    public final Mono<Void> handle(T event) {
        return Mono.fromRunnable(() -> log.debug("Handling event: {} for aggregate: {}",
                        event.getEventType(), event.getAggregateId()))
                .then(doHandle(event))
                .doOnSuccess(result -> log.debug("Successfully handled event: {}", event.getEventType()))
                .doOnError(error -> log.error("Failed to handle event: {}", event.getEventType(), error));
    }

    protected abstract Mono<Void> doHandle(T event);
}
