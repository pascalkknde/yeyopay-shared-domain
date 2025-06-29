package com.yeyopay.shared.infrastructure.repositories;

import com.yeyopay.shared.infrastructure.saga.SagaStep;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SagaStepRepository extends ReactiveCrudRepository<SagaStep, UUID> {

    Flux<SagaStep> findBySagaId(UUID sagaId);

    Mono<SagaStep> findBySagaIdAndStepName(UUID sagaId, String stepName);

    Flux<SagaStep> findBySagaIdAndStatus(UUID sagaId, SagaStep.SagaStepStatus status);

    Flux<SagaStep> findByStatus(SagaStep.SagaStepStatus status);
}
