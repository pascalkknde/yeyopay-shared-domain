package com.yeyopay.shared.domain.exceptions;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessRuleViolationException extends DomainException{
    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_VIOLATION", cause);
    }
}
