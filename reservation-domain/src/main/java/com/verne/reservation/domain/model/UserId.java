package com.verne.reservation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 利用者を一意に識別する値オブジェクトです。
 *
 * @param value UUID形式の識別値
 */
public record UserId(UUID value) {

    /**
     * 識別値がnullでないことを検証します。
     */
    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * 新しい利用者IDを生成します。
     *
     * @return 新しい利用者ID
     */
    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }
}
