package com.yeyopay.shared.infrastructure.scheduling;

import com.yeyopay.shared.infrastructure.events.OutboxEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Scheduled tasks for processing outbox events and maintenance.
 */
@Slf4j
@Component
public class OutboxEventScheduler {

    private final OutboxEventProcessor outboxEventProcessor;

    public OutboxEventScheduler(OutboxEventProcessor outboxEventProcessor) {
        this.outboxEventProcessor = outboxEventProcessor;
    }

    /**
     * Process pending outbox events every 5 seconds.
     */
    @Scheduled(fixedDelay = 5000) // 5 seconds
    public void processPendingEvents() {
        outboxEventProcessor.processPendingEvents()
                .doOnError(error -> log.error("Error processing pending outbox events", error))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }

    /**
     * Retry failed events every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000) // 30 seconds
    public void retryFailedEvents() {
        outboxEventProcessor.retryFailedEvents()
                .doOnError(error -> log.error("Error retrying failed outbox events", error))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }

    /**
     * Cleanup processed events older than 7 days, runs daily at 2 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupProcessedEvents() {
        java.time.Instant cutoffTime = java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);

        // This would need to be implemented in the OutboxEventRepository
        log.info("Scheduled cleanup of processed events older than {}", cutoffTime);
        // outboxEventRepository.cleanupProcessedEvents(cutoffTime)
        //     .doOnSuccess(count -> log.info("Cleaned up {} processed events", count))
        //     .subscribe();
    }
}
