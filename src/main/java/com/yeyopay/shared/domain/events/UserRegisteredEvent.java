package com.yeyopay.shared.domain.events;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserRegisteredEvent extends DomainEvent{
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Instant registeredAt;

    public UserRegisteredEvent(UUID userId, Long version, String email, String firstName, String lastName) {
        super(userId, "User", version);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredAt = getOccurredOn();
    }

    private UserRegisteredEvent(UUID userId, Long version, String email, String firstName,
                                String lastName, UUID correlationId, UUID causationId) {
        super(userId, "User", version, correlationId, causationId);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredAt = getOccurredOn();
    }

    @Override
    public DomainEvent withCorrelationContext(UUID correlationId, UUID causationId) {
        return new UserRegisteredEvent(getAggregateId(), getAggregateVersion(),
                email, firstName, lastName, correlationId, causationId);
    }
}
