package com.yeyopay.shared.domain.base;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public abstract class Entity {
    @Id
    protected UUID id;

    protected Entity() {
        this.id = UUID.randomUUID();
    }

    protected Entity(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id != null && id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
