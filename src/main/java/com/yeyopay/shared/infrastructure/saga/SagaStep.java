package com.yeyopay.shared.infrastructure.saga;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Saga step entity for tracking individual steps within a saga.
 */
@Getter
@Setter
@Table("saga_steps")
public class SagaStep {

    @Id
    @Column("step_id")
    private UUID stepId;

    @Column("saga_id")
    private UUID sagaId;

    @Column("step_name")
    private String stepName;

    @Column("step_type")
    private SagaStepType stepType;

    @Column("step_data")
    private String stepData; // JSON serialized step data

    @Column("status")
    private SagaStepStatus status;

    @Column("retry_count")
    private Integer retryCount;

    @Column("started_at")
    private Instant startedAt;

    @Column("completed_at")
    private Instant completedAt;

    @Column("error_message")
    private String errorMessage;

    public SagaStep() {
        this.stepId = UUID.randomUUID();
        this.status = SagaStepStatus.PENDING;
        this.retryCount = 0;
        this.startedAt = Instant.now();
    }

    public SagaStep(UUID sagaId, String stepName, SagaStepType stepType, String stepData) {
        this();
        this.sagaId = sagaId;
        this.stepName = stepName;
        this.stepType = stepType;
        this.stepData = stepData;
    }

    public void complete() {
        this.status = SagaStepStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.status = SagaStepStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public void compensate() {
        this.status = SagaStepStatus.COMPENSATED;
        this.completedAt = Instant.now();
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public enum SagaStepType {
        COMMAND,      // Forward action
        COMPENSATION  // Rollback action
    }

    public enum SagaStepStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED
    }
}
