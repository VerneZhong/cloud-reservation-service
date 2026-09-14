package com.verne.reservation.application.result;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.ReservationStatus;
import com.verne.reservation.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * 予約ユースケースの実行結果です。
 *
 * @param reservationId 予約ID
 * @param userId 利用者ID
 * @param eventId イベントID
 * @param quantity 予約数
 * @param status 予約状態
 * @param createdAt 作成日時
 * @param expiresAt 有効期限
 * @param confirmedAt 確定日時
 * @param cancelledAt 取消日時
 */
public record ReservationResult(
        ReservationId reservationId,
        UserId userId,
        EventId eventId,
        int quantity,
        ReservationStatus status,
        Instant createdAt,
        Instant expiresAt,
        Instant confirmedAt,
        Instant cancelledAt
) {

    /**
     * 必須項目がnullでないことを検証します。
     */
    public ReservationResult {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    /**
     * ドメインモデルから実行結果を生成します。
     *
     * @param reservation 予約ドメインモデル
     * @return 予約ユースケースの実行結果
     */
    public static ReservationResult from(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation must not be null");
        return new ReservationResult(
                reservation.id(),
                reservation.userId(),
                reservation.eventId(),
                reservation.quantity(),
                reservation.status(),
                reservation.createdAt(),
                reservation.expiresAt(),
                reservation.confirmedAt(),
                reservation.cancelledAt()
        );
    }
}
