package com.yeyopay.shared.infrastructure.serialization;

import com.yeyopay.shared.domain.events.DomainEvent;

public interface EventSerializer {
    String serialize(DomainEvent event);
    DomainEvent deserialize(String eventData, String eventType);
}
