package com.verne.reservation.infrastructure.persistence.entity;

import com.verne.reservation.domain.model.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 予約をPostgreSQLへ保存するJPAエンティティです。
 */
@Getter
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationJpaEntity {

    /** 予約IDです。 */
    @Id
    private UUID id;

    /** 利用者IDです。 */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** イベントIDです。 */
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    /** 予約数です。 */
    @Column(nullable = false)
    private int quantity;

    /** 予約状態です。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    /** 予約作成日時です。 */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 仮予約の有効期限です。 */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** 予約確定日時です。 */
    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    /** 予約取消日時です。 */
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    /** 楽観的ロックに使用するバージョンです。 */
    @Version
    @Column(nullable = false)
    private long version;

    /**
     * 新しい予約エンティティを生成します。
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
    public ReservationJpaEntity(
            UUID id,
            UUID userId,
            UUID eventId,
            int quantity,
            ReservationStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant confirmedAt,
            Instant cancelledAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.eventId = Objects.requireNonNull(eventId);
        update(quantity, status, createdAt, expiresAt, confirmedAt, cancelledAt);
    }

    /**
     * ドメインモデルの内容で予約状態を更新します。
     *
     * @param quantity 予約数
     * @param status 予約状態
     * @param createdAt 作成日時
     * @param expiresAt 有効期限
     * @param confirmedAt 確定日時
     * @param cancelledAt 取消日時
     */
    public void update(
            int quantity,
            ReservationStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant confirmedAt,
            Instant cancelledAt
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }
}
