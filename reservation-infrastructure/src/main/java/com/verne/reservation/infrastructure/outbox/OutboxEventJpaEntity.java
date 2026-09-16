package com.verne.reservation.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * SQSへ送信する前のドメインイベントを保持するOutboxエンティティです。
 */
@Getter
@Entity
@Table(name = "outbox_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventJpaEntity {

    /** OutboxイベントIDです。 */
    @Id
    private UUID id;

    /** ドメインイベントの種類です。 */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /** 集約ルートである予約のIDです。 */
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    /** 利用者IDです。 */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** イベントIDです。 */
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    /** 予約数です。 */
    @Column(nullable = false)
    private int quantity;

    /** ドメインイベント発生日時です。 */
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    /** Outboxへの登録日時です。 */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 外部送信完了日時です。 */
    @Column(name = "published_at")
    private Instant publishedAt;

    /** 楽観的ロックに使用するバージョンです。 */
    @Version
    @Column(nullable = false)
    private long version;

    /**
     * 未送信のOutboxイベントを生成します。
     *
     * @param id OutboxイベントID
     * @param eventType ドメインイベント種別
     * @param aggregateId 予約ID
     * @param userId 利用者ID
     * @param eventId イベントID
     * @param quantity 予約数
     * @param occurredAt イベント発生日時
     * @param createdAt Outbox登録日時
     */
    public OutboxEventJpaEntity(
            UUID id,
            String eventType,
            UUID aggregateId,
            UUID userId,
            UUID eventId,
            int quantity,
            Instant occurredAt,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.eventType = Objects.requireNonNull(eventType);
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.userId = Objects.requireNonNull(userId);
        this.eventId = Objects.requireNonNull(eventId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.occurredAt = Objects.requireNonNull(occurredAt);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    /**
     * 外部送信が完了した日時を記録します。
     *
     * @param publishedAt 外部送信完了日時
     */
    public void markPublished(Instant publishedAt) {
        this.publishedAt = Objects.requireNonNull(publishedAt);
    }
}
