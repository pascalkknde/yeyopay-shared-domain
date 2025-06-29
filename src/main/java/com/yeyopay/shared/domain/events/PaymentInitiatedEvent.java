package com.yeyopay.shared.domain.events;

import com.yeyopay.shared.domain.valueobjects.Money;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class PaymentInitiatedEvent extends DomainEvent {
    private final UUID senderId;
    private final UUID recipientId;
    private final Money amount;
    private final String paymentType;
    private final Instant initiatedAt;

    public PaymentInitiatedEvent(UUID paymentId, Long version, UUID senderId,
                                 UUID recipientId, Money amount, String paymentType) {
        super(paymentId, "Payment", version);
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.initiatedAt = getOccurredOn();
    }

    private PaymentInitiatedEvent(UUID paymentId, Long version, UUID senderId, UUID recipientId,
                                  Money amount, String paymentType, UUID correlationId, UUID causationId) {
        super(paymentId, "Payment", version, correlationId, causationId);
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.initiatedAt = getOccurredOn();
    }

    @Override
    public DomainEvent withCorrelationContext(UUID correlationId, UUID causationId) {
        return new PaymentInitiatedEvent(getAggregateId(), getAggregateVersion(),
                senderId, recipientId, amount, paymentType, correlationId, causationId);
    }
}
