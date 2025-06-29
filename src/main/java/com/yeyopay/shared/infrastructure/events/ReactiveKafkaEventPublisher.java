package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import com.yeyopay.shared.infrastructure.serialization.EventSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Reactive Kafka Event Publisher implementation.
 */
@Slf4j
@Service
public class ReactiveKafkaEventPublisher implements KafkaEventPublisher {
    private final reactor.kafka.sender.KafkaSender<String, Object> kafkaSender;
    private final EventSerializer eventSerializer;

    public ReactiveKafkaEventPublisher(reactor.kafka.sender.KafkaSender<String, Object> kafkaSender,
                                       EventSerializer eventSerializer) {
        this.kafkaSender = kafkaSender;
        this.eventSerializer = eventSerializer;
    }

    @Override
    public Mono<Void> publishEvent(DomainEvent event, String topic, String partitionKey) {
        return Mono.fromCallable(() -> {
                    org.apache.kafka.clients.producer.ProducerRecord<String, Object> record =
                            new org.apache.kafka.clients.producer.ProducerRecord<>(
                                    topic,
                                    partitionKey,
                                    event
                            );

                    // Add headers
                    record.headers().add("eventType", event.getEventType().getBytes());
                    record.headers().add("aggregateType", event.getAggregateType().getBytes());
                    record.headers().add("aggregateId", event.getAggregateId().toString().getBytes());
                    record.headers().add("correlationId",
                            event.getCorrelationId() != null ? event.getCorrelationId().toString().getBytes() : new byte[0]);

                    return reactor.kafka.sender.SenderRecord.create(record, event.getEventId());
                })
                .flatMap(senderRecord ->
                        kafkaSender.send(Mono.just(senderRecord))
                                .doOnNext(result -> {
                                    if (result.exception() != null) {
                                        log.error("Failed to send event to Kafka: {}", event.getEventType(), result.exception());
                                        throw new RuntimeException("Kafka publish failed", result.exception());
                                    } else {
                                        log.debug("Event published to Kafka: {} to topic: {}", event.getEventType(), topic);
                                    }
                                })
                                .then()
                );
    }
}
