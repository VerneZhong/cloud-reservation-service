package com.verne.reservation.infrastructure.persistence.entity;

import com.verne.reservation.domain.model.EventStatus;
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
 * 予約対象イベントをPostgreSQLへ保存するJPAエンティティです。
 */
@Getter
@Entity
@Table(name = "reservable_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservableEventJpaEntity {

    /** イベントIDです。 */
    @Id
    private UUID id;

    /** イベント名です。 */
    @Column(nullable = false, length = 200)
    private String title;

    /** イベントの説明です。 */
    @Column(nullable = false, columnDefinition = "text")
    private String description;

    /** イベント開始日時です。 */
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    /** 予約受付開始日時です。 */
    @Column(name = "booking_opens_at", nullable = false)
    private Instant bookingOpensAt;

    /** 予約受付終了日時です。 */
    @Column(name = "booking_closes_at", nullable = false)
    private Instant bookingClosesAt;

    /** イベント状態です。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    /** 楽観的ロックに使用するバージョンです。 */
    @Version
    @Column(nullable = false)
    private long version;

    /**
     * 新しいイベントエンティティを生成します。
     *
     * @param id イベントID
     * @param title イベント名
     * @param description イベントの説明
     * @param startsAt イベント開始日時
     * @param bookingOpensAt 予約受付開始日時
     * @param bookingClosesAt 予約受付終了日時
     * @param status イベント状態
     */
    public ReservableEventJpaEntity(
            UUID id,
            String title,
            String description,
            Instant startsAt,
            Instant bookingOpensAt,
            Instant bookingClosesAt,
            EventStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        update(title, description, startsAt, bookingOpensAt, bookingClosesAt, status);
    }

    /**
     * ドメインモデルの内容で永続化対象項目を更新します。
     *
     * @param title イベント名
     * @param description イベントの説明
     * @param startsAt イベント開始日時
     * @param bookingOpensAt 予約受付開始日時
     * @param bookingClosesAt 予約受付終了日時
     * @param status イベント状態
     */
    public void update(
            String title,
            String description,
            Instant startsAt,
            Instant bookingOpensAt,
            Instant bookingClosesAt,
            EventStatus status
    ) {
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.startsAt = Objects.requireNonNull(startsAt);
        this.bookingOpensAt = Objects.requireNonNull(bookingOpensAt);
        this.bookingClosesAt = Objects.requireNonNull(bookingClosesAt);
        this.status = Objects.requireNonNull(status);
    }
}
