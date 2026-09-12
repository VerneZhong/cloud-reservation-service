package com.verne.reservation.domain.event;

import java.time.Instant;

/**
 * Marker contract for events raised by the reservation domain.
 */
public interface DomainEvent {

    Instant occurredAt();
}
