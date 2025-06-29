package com.yeyopay.shared.infrastructure.saga;

import com.yeyopay.shared.infrastructure.repositories.SagaInstanceRepository;
import com.yeyopay.shared.infrastructure.repositories.SagaStepRepository;

import java.util.UUID;

/**
 * Default implementation of SagaManager.
 */
@org.springframework.stereotype.Service
public class DefaultSagaManager implements SagaManager {
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultSagaManager.class);

    public DefaultSagaManager(SagaInstanceRepository sagaInstanceRepository,
                              SagaStepRepository sagaStepRepository,
                              com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.sagaStepRepository = sagaStepRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> reactor.core.publisher.Mono<UUID> startSaga(String sagaType, T sagaData, UUID correlationId) {
        return reactor.core.publisher.Mono.fromCallable(() -> {
                    try {
                        String serializedData = objectMapper.writeValueAsString(sagaData);
                        return new SagaInstance(sagaType, serializedData, correlationId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to serialize saga data", e);
                    }
                })
                .flatMap(sagaInstanceRepository::save)
                .doOnSuccess(instance -> log.info("Started saga: {} with ID: {}", sagaType, instance.getSagaId()))
                .map(SagaInstance::getSagaId);
    }

    @Override
    public reactor.core.publisher.Mono<Void> processStep(UUID sagaId, String stepName, Object stepData) {
        return sagaInstanceRepository.findById(sagaId)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new RuntimeException("Saga not found: " + sagaId)))
                .flatMap(saga -> {
                    try {
                        String serializedStepData = objectMapper.writeValueAsString(stepData);
                        SagaStep step = new SagaStep(sagaId, stepName, SagaStep.SagaStepType.COMMAND, serializedStepData);
                        return sagaStepRepository.save(step);
                    } catch (Exception e) {
                        return reactor.core.publisher.Mono.error(
                                new RuntimeException("Failed to serialize step data", e));
                    }
                })
                .doOnSuccess(step -> log.debug("Processing saga step: {} for saga: {}", stepName, sagaId))
                .then();
    }

    @Override
    public reactor.core.publisher.Mono<Void> completeStep(UUID sagaId, String stepName) {
        return sagaStepRepository.findBySagaIdAndStepName(sagaId, stepName)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new RuntimeException("Saga step not found: " + stepName)))
                .doOnNext(SagaStep::complete)
                .flatMap(sagaStepRepository::save)
                .doOnSuccess(step -> log.debug("Completed saga step: {} for saga: {}", stepName, sagaId))
                .then();
    }

    @Override
    public reactor.core.publisher.Mono<Void> failStep(UUID sagaId, String stepName, String errorMessage) {
        return sagaStepRepository.findBySagaIdAndStepName(sagaId, stepName)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new RuntimeException("Saga step not found: " + stepName)))
                .doOnNext(step -> step.fail(errorMessage))
                .flatMap(sagaStepRepository::save)
                .then(startCompensation(sagaId))
                .doOnSuccess(result -> log.warn("Failed saga step: {} for saga: {} - {}", stepName, sagaId, errorMessage));
    }

    @Override
    public reactor.core.publisher.Mono<SagaInstance> getSaga(UUID sagaId) {
        return sagaInstanceRepository.findById(sagaId);
    }

    @Override
    public reactor.core.publisher.Mono<Void> completeSaga(UUID sagaId) {
        return sagaInstanceRepository.findById(sagaId)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new RuntimeException("Saga not found: " + sagaId)))
                .doOnNext(SagaInstance::complete)
                .flatMap(sagaInstanceRepository::save)
                .doOnSuccess(saga -> log.info("Completed saga: {}", sagaId))
                .then();
    }

    @Override
    public reactor.core.publisher.Mono<Void> failSaga(UUID sagaId, String errorDetails) {
        return sagaInstanceRepository.findById(sagaId)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new RuntimeException("Saga not found: " + sagaId)))
                .doOnNext(saga -> saga.fail(errorDetails))
                .flatMap(sagaInstanceRepository::save)
                .doOnSuccess(saga -> log.error("Failed saga: {} - {}", sagaId, errorDetails))
                .then();
    }

    private reactor.core.publisher.Mono<Void> startCompensation(UUID sagaId) {
        return sagaInstanceRepository.findById(sagaId)
                .doOnNext(SagaInstance::startCompensation)
                .flatMap(sagaInstanceRepository::save)
                .then();
    }
}
