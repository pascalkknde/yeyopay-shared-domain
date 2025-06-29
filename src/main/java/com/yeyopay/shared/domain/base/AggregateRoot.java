package com.yeyopay.shared.domain.base;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeyopay.shared.domain.events.DomainEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all Domain Aggregates in the YeyoPay system.
 * Implements event sourcing pattern with domain events.
 */
@Getter
@Setter
public abstract class AggregateRoot<T extends DomainEvent> extends Entity {


    @Version
    protected Long version;

    @JsonIgnore
    private final List<T> domainEvents = new ArrayList<>();

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(UUID id) {
        super(id);
    }

    /**
     * Add a domain event to be published after the aggregate is saved.
     */
    protected void addDomainEvent(T event) {
        this.domainEvents.add(event);
    }

    /**
     * Get all unpublished domain events.
     */
    public List<T> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear all domain events (typically called after publishing).
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * Mark all events as committed and clear them.
     */
    public void markEventsAsCommitted() {
        clearDomainEvents();
    }
}
