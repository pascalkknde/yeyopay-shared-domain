package com.yeyopay.shared.infrastructure.repositories;

import com.yeyopay.shared.infrastructure.eventsourcing.OutboxEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface OutboxEventRepository extends ReactiveCrudRepository<OutboxEvent, UUID> {
    @Query("SELECT * FROM outbox_events WHERE status = :status ORDER BY created_at ASC LIMIT :limit")
    Flux<OutboxEvent> findByStatusOrderByCreatedAt(OutboxEvent.OutboxEventStatus status, Integer limit);

    @Query("SELECT * FROM outbox_events WHERE status = 'FAILED' AND next_retry_at <= :now ORDER BY next_retry_at ASC LIMIT :limit")
    Flux<OutboxEvent> findFailedEventsReadyForRetry(Instant now, Integer limit);

    @Query("UPDATE outbox_events SET status = :status, processed_at = :processedAt WHERE event_id = :eventId")
    Mono<Integer> updateStatus(UUID eventId, OutboxEvent.OutboxEventStatus status, Instant processedAt);

    @Query("UPDATE outbox_events SET status = :status, error_message = :errorMessage, retry_count = :retryCount, next_retry_at = :nextRetryAt WHERE event_id = :eventId")
    Mono<Integer> updateFailedStatus(UUID eventId, OutboxEvent.OutboxEventStatus status, String errorMessage, Integer retryCount, Instant nextRetryAt);

    @Query("DELETE FROM outbox_events WHERE status = 'PROCESSED' AND processed_at < :cutoffTime")
    Mono<Integer> cleanupProcessedEvents(Instant cutoffTime);
}
