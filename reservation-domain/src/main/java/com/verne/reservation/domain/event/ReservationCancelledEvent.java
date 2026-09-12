package com.verne.reservation.domain.event;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * 予約が取り消されたことを表すドメインイベントです。
 *
 * @param reservationId 予約ID
 * @param userId 利用者ID
 * @param eventId イベントID
 * @param quantity 返却対象となる予約数
 * @param occurredAt イベント発生日時
 */
public record ReservationCancelledEvent(
        ReservationId reservationId,
        UserId userId,
        EventId eventId,
        int quantity,
        Instant occurredAt
) implements DomainEvent {

    /**
     * 各項目を検証して予約取消イベントを生成します。
     */
    public ReservationCancelledEvent {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}
