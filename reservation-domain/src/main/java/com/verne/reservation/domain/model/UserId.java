package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 利用者を一意に識別する値オブジェクトです。
 */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }
}
