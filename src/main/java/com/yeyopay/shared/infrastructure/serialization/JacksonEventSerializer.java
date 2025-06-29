package com.yeyopay.shared.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeyopay.shared.domain.events.DomainEvent;

public class JacksonEventSerializer  implements EventSerializer{

    private final ObjectMapper objectMapper;

    public JacksonEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event: " + event.getClass().getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainEvent deserialize(String eventData, String eventType) {
        try {
            Class<? extends DomainEvent> eventClass = (Class<? extends DomainEvent>) Class.forName(
                    "com.yeyopay.shared.domain.events." + eventType
            );
            return objectMapper.readValue(eventData, eventClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event of type: " + eventType, e);
        }
    }
}
