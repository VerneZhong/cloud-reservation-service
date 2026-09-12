package com.verne.reservation.domain.event;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents the creation of a pending reservation.
 */
public record ReservationCreatedEvent(
        ReservationId reservationId,
        UserId userId,
        EventId eventId,
        int quantity,
        Instant occurredAt
) implements DomainEvent {

    public ReservationCreatedEvent {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}
