package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 予約対象イベントを一意に識別する値オブジェクトです。
 *
 * @param value UUID形式の識別値
 */
public record EventId(UUID value) {

    /**
     * 識別値がnullでないことを検証します。
     */
    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * 新しいイベントIDを生成します。
     *
     * @return 新しいイベントID
     */
    public static EventId newId() {
        return new EventId(UUID.randomUUID());
    }
}
