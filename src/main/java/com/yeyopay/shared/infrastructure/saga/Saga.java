package com.yeyopay.shared.infrastructure.saga;

import java.util.UUID;

public abstract class Saga<T> {
    protected UUID sagaId;
    protected T sagaData;
    protected SagaInstance.SagaStatus status;
    protected String currentStep;

    protected Saga(UUID sagaId, T sagaData) {
        this.sagaId = sagaId;
        this.sagaData = sagaData;
        this.status = SagaInstance.SagaStatus.ACTIVE;
    }

    /**
     * Get the saga type name.
     */
    public abstract String getSagaType();

    /**
     * Start the saga execution.
     */
    public abstract void start();

    /**
     * Handle compensation when a step fails.
     */
    public abstract void compensate();

    /**
     * Check if the saga can be completed.
     */
    public abstract boolean canComplete();

    /**
     * Complete the saga.
     */
    public void complete() {
        this.status = SagaInstance.SagaStatus.COMPLETED;
    }

    /**
     * Fail the saga.
     */
    public void fail() {
        this.status = SagaInstance.SagaStatus.FAILED;
    }

    // Getters
    public UUID getSagaId() { return sagaId; }
    public T getSagaData() { return sagaData; }
    public SagaInstance.SagaStatus getStatus() { return status; }
    public String getCurrentStep() { return currentStep; }
}
