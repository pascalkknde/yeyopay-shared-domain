package com.yeyopay.shared.infrastructure.entity;

import com.yeyopay.shared.domain.events.DomainEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Event store entry for persisting domain events.
 */
@Getter
@Setter
@Table("event_store")
public class EventStoreEntry {
    @Id
    @Column("event_id")
    private UUID eventId;

    @Column("stream_id")
    private UUID streamId;

    @Column("stream_name")
    private String streamName;

    @Column("event_type")
    private String eventType;

    @Column("event_data")
    private String eventData; // JSON serialized event

    @Column("event_metadata")
    private String eventMetadata; // JSON serialized metadata

    @Column("stream_version")
    private Long streamVersion;

    @Column("global_version")
    private Long globalVersion;

    @Column("correlation_id")
    private UUID correlationId;

    @Column("causation_id")
    private UUID causationId;

    @Column("occurred_on")
    private Instant occurredOn;

    @Column("created_at")
    private Instant createdAt;

    public EventStoreEntry() {
        this.createdAt = Instant.now();
    }

    public static EventStoreEntry fromDomainEvent(DomainEvent event, String eventData) {
        EventStoreEntry entry = new EventStoreEntry();
        entry.setEventId(event.getEventId());
        entry.setStreamId(event.getAggregateId());
        entry.setStreamName(event.getStreamName());
        entry.setEventType(event.getEventType());
        entry.setEventData(eventData);
        entry.setStreamVersion(event.getAggregateVersion());
        entry.setCorrelationId(event.getCorrelationId());
        entry.setCausationId(event.getCausationId());
        entry.setOccurredOn(event.getOccurredOn());
        return entry;
    }
}
