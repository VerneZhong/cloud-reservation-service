package com.verne.reservation.domain.event;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredAt();
}
