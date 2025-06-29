package com.yeyopay.shared.infrastructure.saga;

import java.util.UUID;

/**
 * Saga Manager for orchestrating sagas.
 */
public interface SagaManager {

    /**
     * Start a new saga.
     */
    <T> reactor.core.publisher.Mono<UUID> startSaga(String sagaType, T sagaData, UUID correlationId);

    /**
     * Process a saga step.
     */
    reactor.core.publisher.Mono<Void> processStep(UUID sagaId, String stepName, Object stepData);

    /**
     * Complete a saga step.
     */
    reactor.core.publisher.Mono<Void> completeStep(UUID sagaId, String stepName);

    /**
     * Fail a saga step and trigger compensation.
     */
    reactor.core.publisher.Mono<Void> failStep(UUID sagaId, String stepName, String errorMessage);

    /**
     * Get saga instance by ID.
     */
    reactor.core.publisher.Mono<SagaInstance> getSaga(UUID sagaId);

    /**
     * Complete a saga.
     */
    reactor.core.publisher.Mono<Void> completeSaga(UUID sagaId);

    /**
     * Fail a saga.
     */
    reactor.core.publisher.Mono<Void> failSaga(UUID sagaId, String errorDetails);
}
