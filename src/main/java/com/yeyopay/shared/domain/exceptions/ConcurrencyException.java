package com.yeyopay.shared.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when there's a concurrency conflict.
 */
public class ConcurrencyException extends DomainException {
    public ConcurrencyException(String message) {
        super(message, "CONCURRENCY_CONFLICT");
    }

    public ConcurrencyException(String aggregateType, UUID aggregateId, Long expectedVersion, Long actualVersion) {
        super(String.format("Concurrency conflict for %s with ID %s. Expected version: %d, Actual version: %d",
                aggregateType, aggregateId, expectedVersion, actualVersion), "CONCURRENCY_CONFLICT");
    }
}
