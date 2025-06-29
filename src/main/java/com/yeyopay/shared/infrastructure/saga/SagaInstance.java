package com.yeyopay.shared.infrastructure.saga;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Saga instance entity for tracking long-running business processes.
 */
@Getter
@Setter
@Table("saga_instances")
public class SagaInstance {
    @Id
    @Column("saga_id")
    private UUID sagaId;

    @Column("saga_type")
    private String sagaType;

    @Column("saga_data")
    private String sagaData; // JSON serialized saga state

    @Column("current_step")
    private String currentStep;

    @Column("status")
    private SagaStatus status;

    @Column("correlation_id")
    private UUID correlationId;

    @Column("started_at")
    private Instant startedAt;

    @Column("completed_at")
    private Instant completedAt;

    @Column("timeout_at")
    private Instant timeoutAt;

    @Column("retry_count")
    private Integer retryCount;

    @Column("error_details")
    private String errorDetails; // JSON serialized error information

    public SagaInstance() {
        this.sagaId = UUID.randomUUID();
        this.status = SagaStatus.ACTIVE;
        this.retryCount = 0;
        this.startedAt = Instant.now();
    }

    public SagaInstance(String sagaType, String sagaData, UUID correlationId) {
        this();
        this.sagaType = sagaType;
        this.sagaData = sagaData;
        this.correlationId = correlationId;
    }

    public void complete() {
        this.status = SagaStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void fail(String errorDetails) {
        this.status = SagaStatus.FAILED;
        this.errorDetails = errorDetails;
        this.completedAt = Instant.now();
    }

    public void startCompensation() {
        this.status = SagaStatus.COMPENSATING;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public enum SagaStatus {
        ACTIVE,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED,
        TIMEOUT
    }

}
