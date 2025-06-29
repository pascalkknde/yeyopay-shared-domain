package com.yeyopay.shared.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class KycStatusChangedEvent extends DomainEvent {

    private final String previousStatus;
    private final String newStatus;
    private final Integer kycLevel;
    private final String reason;
    private final UUID reviewedBy;
    private final Instant changedAt;

    public KycStatusChangedEvent(UUID userId, Long version, String previousStatus, String newStatus,
                                 Integer kycLevel, String reason, UUID reviewedBy) {
        super(userId, "User", version);
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.kycLevel = kycLevel;
        this.reason = reason;
        this.reviewedBy = reviewedBy;
        this.changedAt = getOccurredOn();
    }

    private KycStatusChangedEvent(UUID userId, Long version, String previousStatus, String newStatus,
                                  Integer kycLevel, String reason, UUID reviewedBy,
                                  UUID correlationId, UUID causationId) {
        super(userId, "User", version, correlationId, causationId);
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.kycLevel = kycLevel;
        this.reason = reason;
        this.reviewedBy = reviewedBy;
        this.changedAt = getOccurredOn();
    }

    @Override
    public DomainEvent withCorrelationContext(UUID correlationId, UUID causationId) {
        return new KycStatusChangedEvent(getAggregateId(), getAggregateVersion(),
                previousStatus, newStatus, kycLevel, reason, reviewedBy,
                correlationId, causationId);
    }
}
