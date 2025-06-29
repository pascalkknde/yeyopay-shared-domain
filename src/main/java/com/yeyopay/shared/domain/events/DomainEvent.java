package com.yeyopay.shared.domain.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;



/**
 * Base class for all domain events in the YeyoPay system.
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "UserRegistered"),
        @JsonSubTypes.Type(value = PaymentInitiatedEvent.class, name = "PaymentInitiated"),
        @JsonSubTypes.Type(value = WalletCreatedEvent.class, name = "WalletCreated"),
        @JsonSubTypes.Type(value = KycStatusChangedEvent.class, name = "KycStatusChanged"),
        @JsonSubTypes.Type(value = LoanApplicationSubmittedEvent.class, name = "LoanApplicationSubmitted")
})
public abstract class DomainEvent {
    private final UUID eventId;
    private final UUID aggregateId;
    private final String aggregateType;
    private final Long aggregateVersion;
    private final Instant occurredOn;
    private final UUID correlationId;
    private final UUID causationId;
    private final String eventType;

    protected DomainEvent(UUID aggregateId, String aggregateType, Long aggregateVersion) {
        this(aggregateId, aggregateType, aggregateVersion, null, null);
    }

    protected DomainEvent(UUID aggregateId, String aggregateType, Long aggregateVersion,
                          UUID correlationId, UUID causationId) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.aggregateVersion = aggregateVersion;
        this.occurredOn = Instant.now();
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * Create a new event with correlation context.
     */
    public abstract DomainEvent withCorrelationContext(UUID correlationId, UUID causationId);

    /**
     * Get the stream name for this event (typically aggregate type + aggregate id).
     */
    public String getStreamName() {
        return aggregateType + "-" + aggregateId;
    }
}
