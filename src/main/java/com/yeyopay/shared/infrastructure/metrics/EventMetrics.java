package com.yeyopay.shared.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class EventMetrics {

    private final Counter eventsPublished;
    private final Counter eventsProcessed;
    private final Counter eventsFailed;
    private final Timer eventProcessingTime;
    private final AtomicLong pendingEventsGauge;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.eventsPublished = Counter.builder("yeyopay.events.published")
                .description("Number of events published to outbox")
                .register(meterRegistry);

        this.eventsProcessed = Counter.builder("yeyopay.events.processed")
                .description("Number of events successfully processed from outbox")
                .register(meterRegistry);

        this.eventsFailed = Counter.builder("yeyopay.events.failed")
                .description("Number of events that failed processing")
                .register(meterRegistry);

        this.eventProcessingTime = Timer.builder("yeyopay.events.processing.time")
                .description("Time taken to process events")
                .register(meterRegistry);

        this.pendingEventsGauge = new AtomicLong(0);

        Gauge.builder("yeyopay.events.pending", pendingEventsGauge, AtomicLong::get)
                .description("Number of pending events in outbox")
                .register(meterRegistry);
    }

    public void incrementEventsPublished() {
        eventsPublished.increment();
    }

    public void incrementEventsProcessed() {
        eventsProcessed.increment();
    }

    public void incrementEventsFailed() {
        eventsFailed.increment();
    }

    public Timer.Sample startEventProcessingTimer() {
        return Timer.start();
    }

    public void stopEventProcessingTimer(Timer.Sample sample) {
        sample.stop(eventProcessingTime);
    }

    public void updatePendingEventsCount(long count) {
        pendingEventsGauge.set(count);
    }
}
