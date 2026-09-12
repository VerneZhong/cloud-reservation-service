package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 予約対象イベントを一意に識別する値オブジェクトです。
 */
public record EventId(UUID value) {

    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EventId newId() {
        return new EventId(UUID.randomUUID());
    }
}
