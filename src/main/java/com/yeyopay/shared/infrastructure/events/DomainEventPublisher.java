package com.yeyopay.shared.infrastructure.events;

import com.yeyopay.shared.domain.events.DomainEvent;
import com.yeyopay.shared.infrastructure.eventsourcing.OutboxEvent;
import com.yeyopay.shared.infrastructure.repositories.OutboxEventRepository;
import com.yeyopay.shared.infrastructure.serialization.EventSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain Event Publisher using the Outbox Pattern for reliable event publishing.
 */
@Slf4j
@Service
public class DomainEventPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final EventSerializer eventSerializer;
    private final Map<String, String> eventTopicMapping;

    public DomainEventPublisher(OutboxEventRepository outboxEventRepository,
                                EventSerializer eventSerializer) {
        this.outboxEventRepository = outboxEventRepository;
        this.eventSerializer = eventSerializer;
        this.eventTopicMapping = initializeEventTopicMapping();
    }

    /**
     * Publish a single domain event using the outbox pattern.
     */
    public Mono<Void> publishEvent(DomainEvent event) {
        return Mono.fromCallable(() -> {
                    String eventData = eventSerializer.serialize(event);
                    String topic = getTopicForEvent(event);
                    return OutboxEvent.fromDomainEvent(event, eventData, topic);
                })
                .flatMap(outboxEventRepository::save)
                .doOnSuccess(outboxEvent -> log.debug("Event published to outbox: {}", outboxEvent.getEventType()))
                .doOnError(error -> log.error("Failed to publish event to outbox: {}", event.getEventType(), error))
                .then();
    }

    /**
     * Publish multiple domain events in a single transaction.
     */
    public Mono<Void> publishEvents(List<DomainEvent> events) {
        return Flux.fromIterable(events)
                .flatMap(event -> {
                    String eventData = eventSerializer.serialize(event);
                    String topic = getTopicForEvent(event);
                    OutboxEvent outboxEvent = OutboxEvent.fromDomainEvent(event, eventData, topic);
                    return outboxEventRepository.save(outboxEvent);
                })
                .doOnNext(outboxEvent -> log.debug("Event published to outbox: {}", outboxEvent.getEventType()))
                .doOnError(error -> log.error("Failed to publish events to outbox", error))
                .then();
    }

    /**
     * Get the Kafka topic for a given event type.
     */
    private String getTopicForEvent(DomainEvent event) {
        String eventType = event.getEventType();
        return eventTopicMapping.getOrDefault(eventType, getDefaultTopic(event));
    }

    /**
     * Get default topic based on aggregate type.
     */
    private String getDefaultTopic(DomainEvent event) {
        String aggregateType = event.getAggregateType().toLowerCase();
        return "yeyopay." + aggregateType + ".events";
    }

    /**
     * Initialize event to topic mapping.
     */
    private Map<String, String> initializeEventTopicMapping() {
        Map<String, String> eventTopicMap = new HashMap<>();

        // User Events
        eventTopicMap.put("UserRegisteredEvent", "yeyopay.user.events");
        eventTopicMap.put("KycStatusChangedEvent", "yeyopay.user.events");

        // Payment Events
        eventTopicMap.put("PaymentInitiatedEvent", "yeyopay.payment.events");
        eventTopicMap.put("PaymentCompletedEvent", "yeyopay.payment.events");
        eventTopicMap.put("PaymentFailedEvent", "yeyopay.payment.events");

        // Wallet Events
        eventTopicMap.put("WalletCreatedEvent", "yeyopay.wallet.events");
        eventTopicMap.put("WalletCreditedEvent", "yeyopay.wallet.events");
        eventTopicMap.put("WalletDebitedEvent", "yeyopay.wallet.events");

        // Loan Events
        eventTopicMap.put("LoanApplicationSubmittedEvent", "yeyopay.loan.events");
        eventTopicMap.put("LoanApprovedEvent", "yeyopay.loan.events");
        eventTopicMap.put("LoanDisbursedEvent", "yeyopay.loan.events");

        // Fraud Events
        eventTopicMap.put("FraudAlertCreatedEvent", "yeyopay.fraud.events");
        eventTopicMap.put("RiskAssessmentCompletedEvent", "yeyopay.fraud.events");

        // Settlement Events
        eventTopicMap.put("SettlementInitiatedEvent", "yeyopay.settlement.events");
        eventTopicMap.put("SettlementCompletedEvent", "yeyopay.settlement.events");

        // Notification Events
        eventTopicMap.put("NotificationSentEvent", "yeyopay.notification.events");

        // Blockchain Events
        eventTopicMap.put("BlockchainTransactionCreatedEvent", "yeyopay.blockchain.events");

        return eventTopicMap;
    }

}
