package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InvalidReservationStateException;

import java.time.Instant;
import java.util.Objects;

/**
 * 予約情報とライフサイクルの状態遷移を管理する集約です。
 */
public final class Reservation {

    /**
     * 予約IDです。
     */
    private final ReservationId id;
    /**
     * 予約を行った利用者のIDです。
     */
    private final UserId userId;
    /**
     * 予約対象イベントのIDです。
     */
    private final EventId eventId;
    /**
     * 予約数です。
     */
    private final int quantity;
    /**
     * 予約作成日時です。
     */
    private final Instant createdAt;
    /**
     * 仮予約の有効期限です。
     */
    private final Instant expiresAt;
    /**
     * 現在の予約状態です。
     */
    private ReservationStatus status;
    /**
     * 予約確定日時です。未確定の場合はnullです。
     */
    private Instant confirmedAt;
    /**
     * 予約取消日時です。未取消の場合はnullです。
     */
    private Instant cancelledAt;

    /**
     * 予約情報を生成または永続化データから復元します。
     *
     * @param id 予約ID
     * @param userId 利用者ID
     * @param eventId イベントID
     * @param quantity 予約数
     * @param status 予約状態
     * @param createdAt 作成日時
     * @param expiresAt 有効期限
     * @param confirmedAt 確定日時
     * @param cancelledAt 取消日時
     */
    public Reservation(
            ReservationId id,
            UserId userId,
            EventId eventId,
            int quantity,
            ReservationStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant confirmedAt,
            Instant cancelledAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    /**
     * 新しい仮予約を生成します。
     *
     * @param id 予約ID
     * @param userId 利用者ID
     * @param eventId イベントID
     * @param quantity 予約数
     * @param createdAt 作成日時
     * @param expiresAt 有効期限
     * @return 仮予約
     */
    public static Reservation createPending(
            ReservationId id,
            UserId userId,
            EventId eventId,
            int quantity,
            Instant createdAt,
            Instant expiresAt
    ) {
        return new Reservation(
                id,
                userId,
                eventId,
                quantity,
                ReservationStatus.PENDING,
                createdAt,
                expiresAt,
                null,
                null
        );
    }

    /**
     * 有効期限内の仮予約を確定します。
     *
     * @param now 確定日時
     */
    public void confirm(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        requireStatus(ReservationStatus.PENDING, "confirm");
        if (!now.isBefore(expiresAt)) {
            throw new InvalidReservationStateException(status, "confirm an expired");
        }
        status = ReservationStatus.CONFIRMED;
        confirmedAt = now;
    }

    /**
     * 仮予約または確定済み予約を取り消します。
     *
     * @param now 取消日時
     */
    public void cancel(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (status != ReservationStatus.PENDING && status != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStateException(status, "cancel");
        }
        status = ReservationStatus.CANCELLED;
        cancelledAt = now;
    }

    /**
     * 有効期限を迎えた仮予約を失効させます。
     *
     * @param now 判定日時
     */
    public void expire(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        requireStatus(ReservationStatus.PENDING, "expire");
        if (now.isBefore(expiresAt)) {
            throw new IllegalStateException("Reservation has not reached its expiration time");
        }
        status = ReservationStatus.EXPIRED;
    }

    /**
     * 現在の予約状態が要求された状態と一致することを検証します。
     *
     * @param requiredStatus 要求される状態
     * @param operation 実行対象の操作
     */
    private void requireStatus(ReservationStatus requiredStatus, String operation) {
        if (status != requiredStatus) {
            throw new InvalidReservationStateException(status, operation);
        }
    }

    /**
     * 予約IDを返します。
     *
     * @return 予約ID
     */
    public ReservationId id() {
        return id;
    }

    /**
     * 利用者IDを返します。
     *
     * @return 利用者ID
     */
    public UserId userId() {
        return userId;
    }

    /**
     * イベントIDを返します。
     *
     * @return イベントID
     */
    public EventId eventId() {
        return eventId;
    }

    /**
     * 予約数を返します。
     *
     * @return 予約数
     */
    public int quantity() {
        return quantity;
    }

    /**
     * 予約状態を返します。
     *
     * @return 予約状態
     */
    public ReservationStatus status() {
        return status;
    }

    /**
     * 作成日時を返します。
     *
     * @return 作成日時
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * 有効期限を返します。
     *
     * @return 有効期限
     */
    public Instant expiresAt() {
        return expiresAt;
    }

    /**
     * 確定日時を返します。
     *
     * @return 確定日時
     */
    public Instant confirmedAt() {
        return confirmedAt;
    }

    /**
     * 取消日時を返します。
     *
     * @return 取消日時
     */
    public Instant cancelledAt() {
        return cancelledAt;
    }
}
