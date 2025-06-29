package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import reactor.core.publisher.Mono;

public interface EventHandler<T extends DomainEvent> {
    Mono<Void> handle(T event);

    /**
     * Get the event type this handler processes.
     */
    default Class<T> getEventType() {
        return (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
    }
}
