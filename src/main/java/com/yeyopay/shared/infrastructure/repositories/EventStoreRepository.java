package com.yeyopay.shared.infrastructure.repositories;

import com.yeyopay.shared.infrastructure.entity.EventStoreEntry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface EventStoreRepository extends ReactiveCrudRepository<EventStoreEntry, UUID> {
    @Query("SELECT * FROM event_store WHERE stream_id = :streamId ORDER BY stream_version ASC")
    Flux<EventStoreEntry> findByStreamIdOrderByStreamVersion(UUID streamId);

    @Query("SELECT * FROM event_store WHERE stream_id = :streamId AND stream_version >= :fromVersion ORDER BY stream_version ASC")
    Flux<EventStoreEntry> findByStreamIdFromVersion(UUID streamId, Long fromVersion);

    @Query("SELECT * FROM event_store WHERE stream_name = :streamName ORDER BY stream_version ASC")
    Flux<EventStoreEntry> findByStreamNameOrderByStreamVersion(String streamName);

    @Query("SELECT MAX(stream_version) FROM event_store WHERE stream_id = :streamId")
    Mono<Long> getLatestStreamVersion(UUID streamId);

    @Query("SELECT MAX(global_version) FROM event_store")
    Mono<Long> getLatestGlobalVersion();

    @Query("SELECT * FROM event_store WHERE global_version > :fromVersion ORDER BY global_version ASC LIMIT :limit")
    Flux<EventStoreEntry> findEventsFromGlobalVersion(Long fromVersion, Integer limit);

    @Query("SELECT * FROM event_store WHERE event_type = :eventType AND occurred_on >= :fromTime ORDER BY occurred_on ASC")
    Flux<EventStoreEntry> findEventsByTypeFromTime(String eventType, Instant fromTime);
}
