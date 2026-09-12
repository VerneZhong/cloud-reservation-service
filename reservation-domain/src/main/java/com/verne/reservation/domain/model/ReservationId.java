package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 予約を一意に識別する値オブジェクトです。
 */
public record ReservationId(UUID value) {

    public ReservationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ReservationId newId() {
        return new ReservationId(UUID.randomUUID());
    }
}
