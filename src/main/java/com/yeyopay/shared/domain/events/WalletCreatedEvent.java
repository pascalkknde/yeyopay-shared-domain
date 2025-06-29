package com.yeyopay.shared.domain.events;

import com.yeyopay.shared.domain.valueobjects.Currency;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class WalletCreatedEvent extends DomainEvent{
    private final UUID userId;
    private final Currency currency;
    private final String walletType;
    private final Instant createdAt;

    public WalletCreatedEvent(UUID walletId, Long version, UUID userId, Currency currency, String walletType) {
        super(walletId, "Wallet", version);
        this.userId = userId;
        this.currency = currency;
        this.walletType = walletType;
        this.createdAt = getOccurredOn();
    }

    private WalletCreatedEvent(UUID walletId, Long version, UUID userId, Currency currency,
                               String walletType, UUID correlationId, UUID causationId) {
        super(walletId, "Wallet", version, correlationId, causationId);
        this.userId = userId;
        this.currency = currency;
        this.walletType = walletType;
        this.createdAt = getOccurredOn();
    }

    @Override
    public DomainEvent withCorrelationContext(UUID correlationId, UUID causationId) {
        return new WalletCreatedEvent(getAggregateId(), getAggregateVersion(),
                userId, currency, walletType, correlationId, causationId);
    }
}
