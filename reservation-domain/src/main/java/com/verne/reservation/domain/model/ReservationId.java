package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 予約を一意に識別する値オブジェクトです。
 *
 * @param value UUID形式の識別値
 */
public record ReservationId(UUID value) {

    /**
     * 識別値がnullでないことを検証します。
     */
    public ReservationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * 新しい予約IDを生成します。
     *
     * @return 新しい予約ID
     */
    public static ReservationId newId() {
        return new ReservationId(UUID.randomUUID());
    }
}
