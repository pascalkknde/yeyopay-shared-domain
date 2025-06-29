package com.yeyopay.shared.infrastructure.eventsourcing;

import com.yeyopay.shared.domain.events.DomainEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("outbox_events")
public class OutboxEvent {

    @Id
    @Column("event_id")
    private UUID eventId;

    @Column("aggregate_type")
    private String aggregateType;

    @Column("aggregate_id")
    private UUID aggregateId;

    @Column("event_type")
    private String eventType;

    @Column("event_data")
    private String eventData; // JSON serialized event

    @Column("correlation_id")
    private UUID correlationId;

    @Column("causation_id")
    private UUID causationId;

    @Column("topic")
    private String topic;

    @Column("partition_key")
    private String partitionKey;

    @Column("status")
    private OutboxEventStatus status;

    @Column("retry_count")
    private Integer retryCount;

    @Column("created_at")
    private Instant createdAt;

    @Column("processed_at")
    private Instant processedAt;

    @Column("next_retry_at")
    private Instant nextRetryAt;

    @Column("error_message")
    private String errorMessage;

    public OutboxEvent() {
        this.eventId = UUID.randomUUID();
        this.status = OutboxEventStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
    }

    public static OutboxEvent fromDomainEvent(DomainEvent event, String eventData, String topic) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType(event.getAggregateType());
        outboxEvent.setAggregateId(event.getAggregateId());
        outboxEvent.setEventType(event.getEventType());
        outboxEvent.setEventData(eventData);
        outboxEvent.setCorrelationId(event.getCorrelationId());
        outboxEvent.setCausationId(event.getCausationId());
        outboxEvent.setTopic(topic);
        outboxEvent.setPartitionKey(event.getAggregateId().toString());
        return outboxEvent;
    }

    public void markAsProcessed() {
        this.status = OutboxEventStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = OutboxEventStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
        this.nextRetryAt = Instant.now().plusSeconds(calculateRetryDelay());
    }

    private long calculateRetryDelay() {
        // Exponential backoff: 2^retryCount seconds, max 300 seconds (5 minutes)
        return Math.min(300, (long) Math.pow(2, retryCount));
    }

    public enum OutboxEventStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED,
        EXPIRED
    }

}
