package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a user.
 */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }
}
