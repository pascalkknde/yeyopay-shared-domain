package com.yeyopay.shared.domain.events;

import com.yeyopay.shared.domain.valueobjects.Money;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class LoanApplicationSubmittedEvent extends DomainEvent {
    private final UUID borrowerId;
    private final Money requestedAmount;
    private final String loanType;
    private final Integer termMonths;
    private final String purpose;
    private final Instant submittedAt;

    public LoanApplicationSubmittedEvent(UUID loanId, Long version, UUID borrowerId,
                                         Money requestedAmount, String loanType,
                                         Integer termMonths, String purpose) {
        super(loanId, "Loan", version);
        this.borrowerId = borrowerId;
        this.requestedAmount = requestedAmount;
        this.loanType = loanType;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.submittedAt = getOccurredOn();
    }

    private LoanApplicationSubmittedEvent(UUID loanId, Long version, UUID borrowerId,
                                          Money requestedAmount, String loanType, Integer termMonths,
                                          String purpose, UUID correlationId, UUID causationId) {
        super(loanId, "Loan", version, correlationId, causationId);
        this.borrowerId = borrowerId;
        this.requestedAmount = requestedAmount;
        this.loanType = loanType;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.submittedAt = getOccurredOn();
    }

    @Override
    public DomainEvent withCorrelationContext(UUID correlationId, UUID causationId) {
        return new LoanApplicationSubmittedEvent(getAggregateId(), getAggregateVersion(),
                borrowerId, requestedAmount, loanType, termMonths,
                purpose, correlationId, causationId);
    }
}
