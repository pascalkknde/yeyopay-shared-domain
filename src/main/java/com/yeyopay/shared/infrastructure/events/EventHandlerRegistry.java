package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * Event Handler Registry for managing event handlers across services.
 */
@Service
@Slf4j
public class EventHandlerRegistry {
    private final Map<String, List<EventHandler<? extends DomainEvent>>> handlers = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Register an event handler for a specific event type.
     */
    public <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler) {
        String eventTypeName = eventType.getSimpleName();
        handlers.computeIfAbsent(eventTypeName, k -> new java.util.ArrayList<>()).add(handler);
    }

    /**
     * Get all handlers for a specific event type.
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> List<EventHandler<T>> getHandlers(Class<T> eventType) {
        String eventTypeName = eventType.getSimpleName();
        return handlers.getOrDefault(eventTypeName, java.util.Collections.emptyList())
                .stream()
                .map(handler -> (EventHandler<T>) handler)
                .toList();
    }

    /**
     * Handle an event by invoking all registered handlers.
     */
    public <T extends DomainEvent> Mono<Void> handleEvent(T event) {
        List<EventHandler<T>> eventHandlers = getHandlers((Class<T>) event.getClass());

        return Flux.fromIterable(eventHandlers)
                .flatMap(handler -> handler.handle(event)
                        .doOnError(error -> log.error("Event handler failed for event: {}", event.getEventType(), error))
                        .onErrorResume(error -> Mono.empty()) // Continue with other handlers even if one fails
                )
                .then();
    }
}
