package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import com.yeyopay.shared.infrastructure.eventsourcing.OutboxEvent;
import com.yeyopay.shared.infrastructure.repositories.OutboxEventRepository;
import com.yeyopay.shared.infrastructure.serialization.EventSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Event Handler for processing outbox events and publishing to Kafka.
 */
@Slf4j
@Service
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final EventSerializer eventSerializer;

    public OutboxEventProcessor(OutboxEventRepository outboxEventRepository,
                                KafkaEventPublisher kafkaEventPublisher,
                                EventSerializer eventSerializer) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.eventSerializer = eventSerializer;
    }

    /**
     * Process pending events from the outbox and publish to Kafka.
     */
    public Mono<Void> processPendingEvents() {
        return outboxEventRepository.findByStatusOrderByCreatedAt(OutboxEvent.OutboxEventStatus.PENDING, 100)
                .flatMap(this::processOutboxEvent)
                .then();
    }

    /**
     * Retry failed events that are ready for retry.
     */
    public Mono<Void> retryFailedEvents() {
        return outboxEventRepository.findFailedEventsReadyForRetry(java.time.Instant.now(), 50)
                .flatMap(this::processOutboxEvent)
                .then();
    }

    private Mono<Void> processOutboxEvent(OutboxEvent outboxEvent) {
        return Mono.fromRunnable(() -> outboxEvent.setStatus(OutboxEvent.OutboxEventStatus.PROCESSING))
                .then(outboxEventRepository.save(outboxEvent))
                .then(publishToKafka(outboxEvent))
                .doOnSuccess(result -> outboxEvent.markAsProcessed())
                .doOnError(error -> {
                    log.error("Failed to publish event to Kafka: {}", outboxEvent.getEventType(), error);
                    outboxEvent.markAsFailed(error.getMessage());
                })
                .then(outboxEventRepository.save(outboxEvent))
                .then();
    }

    private Mono<Void> publishToKafka(OutboxEvent outboxEvent) {
        try {
            DomainEvent event = eventSerializer.deserialize(outboxEvent.getEventData(), outboxEvent.getEventType());
            return kafkaEventPublisher.publishEvent(event, outboxEvent.getTopic(), outboxEvent.getPartitionKey());
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to deserialize event: " + outboxEvent.getEventType(), e));
        }
    }
}
