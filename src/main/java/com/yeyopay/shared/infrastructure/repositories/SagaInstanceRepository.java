package com.yeyopay.shared.infrastructure.repositories;

import com.yeyopay.shared.infrastructure.saga.SagaInstance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface SagaInstanceRepository extends ReactiveCrudRepository<SagaInstance, UUID> {

    Flux<SagaInstance> findByStatus(SagaInstance.SagaStatus status);
    Flux<SagaInstance> findBySagaType(String sagaType);

    @Query("SELECT * FROM saga_instances WHERE status = 'ACTIVE' AND timeout_at < :now")
    Flux<SagaInstance> findTimedOutSagas(Instant now);

    Mono<SagaInstance> findByCorrelationId(UUID correlationId);
}
