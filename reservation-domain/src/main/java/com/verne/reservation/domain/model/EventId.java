package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a reservable event.
 */
public record EventId(UUID value) {

    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EventId newId() {
        return new EventId(UUID.randomUUID());
    }
}
