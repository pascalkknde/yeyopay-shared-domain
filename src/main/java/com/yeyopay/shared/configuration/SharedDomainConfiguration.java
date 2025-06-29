package com.yeyopay.shared.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeyopay.shared.infrastructure.events.DomainEventPublisher;
import com.yeyopay.shared.infrastructure.repositories.OutboxEventRepository;
import com.yeyopay.shared.infrastructure.serialization.EventSerializer;
import com.yeyopay.shared.infrastructure.serialization.JacksonEventSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for shared domain infrastructure.
 */
@Configuration
public class SharedDomainConfiguration {
    @Bean
    public EventSerializer eventSerializer(ObjectMapper objectMapper) {
        return new JacksonEventSerializer(objectMapper);
    }

    @Bean
    public DomainEventPublisher domainEventPublisher(OutboxEventRepository outboxEventRepository,
                                                     EventSerializer eventSerializer) {
        return new DomainEventPublisher(outboxEventRepository, eventSerializer);
    }
}
