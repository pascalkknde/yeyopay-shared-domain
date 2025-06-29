package com.yeyopay.shared.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when an aggregate is not found.
 */
public class AggregateNotFoundException extends DomainException{
    public AggregateNotFoundException(String aggregateType, UUID aggregateId) {
        super(String.format("%s with ID %s not found", aggregateType, aggregateId), "AGGREGATE_NOT_FOUND");
    }

    public AggregateNotFoundException(String message) {
        super(message, "AGGREGATE_NOT_FOUND");
    }
}
