package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import reactor.core.publisher.Mono;
/**
 * Kafka Event Publisher for sending events to Kafka topics.
 */
public interface KafkaEventPublisher {
    Mono<Void> publishEvent(DomainEvent event, String topic, String partitionKey);
}
