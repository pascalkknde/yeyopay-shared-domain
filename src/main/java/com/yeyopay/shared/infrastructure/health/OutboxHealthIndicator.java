package com.yeyopay.shared.infrastructure.health;


import com.yeyopay.shared.infrastructure.repositories.OutboxEventRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Health check for outbox event processing.
 */
@Component
public class OutboxHealthIndicator implements HealthIndicator {

    private final OutboxEventRepository outboxEventRepository;
    private static final Duration STALE_EVENT_THRESHOLD = Duration.ofMinutes(5);

    public OutboxHealthIndicator(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    public Health health() {
        try {
            // Check for stale pending events
            Instant staleThreshold = Instant.now().minus(STALE_EVENT_THRESHOLD);

            long staleEventCount = outboxEventRepository
                    .findByStatusOrderByCreatedAt(
                            com.yeyopay.shared.infrastructure.eventsourcing.OutboxEvent.OutboxEventStatus.PENDING,
                            1000
                    )
                    .filter(event -> event.getCreatedAt().isBefore(staleThreshold))
                    .count()
                    .block(Duration.ofSeconds(5));

            if (staleEventCount > 10) {
                return Health.down()
                        .withDetail("staleEvents", staleEventCount)
                        .withDetail("message", "Too many stale outbox events")
                        .build();
            } else if (staleEventCount > 0) {
                return Health.up()
                        .withDetail("staleEvents", staleEventCount)
                        .withDetail("message", "Some stale events present but within acceptable limits")
                        .build();
            } else {
                return Health.up()
                        .withDetail("staleEvents", 0)
                        .withDetail("message", "Outbox processing healthy")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
