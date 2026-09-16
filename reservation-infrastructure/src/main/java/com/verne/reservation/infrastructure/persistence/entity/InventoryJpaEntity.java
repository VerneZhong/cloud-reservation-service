package com.verne.reservation.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 * イベント在庫をPostgreSQLへ保存するJPAエンティティです。
 */
@Getter
@Entity
@Table(name = "event_inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryJpaEntity {

    /** 在庫を管理するイベントIDです。 */
    @Id
    @Column(name = "event_id")
    private UUID eventId;

    /** イベントの総定員です。 */
    @Column(nullable = false)
    private int capacity;

    /** 現在の予約可能数です。 */
    @Column(nullable = false)
    private int available;

    /** 楽観的ロックに使用するバージョンです。 */
    @Version
    @Column(nullable = false)
    private long version;

    /**
     * 新しい在庫エンティティを生成します。
     *
     * @param eventId イベントID
     * @param capacity 総定員
     * @param available 予約可能数
     */
    public InventoryJpaEntity(UUID eventId, int capacity, int available) {
        this.eventId = Objects.requireNonNull(eventId);
        update(capacity, available);
    }

    /**
     * ドメインモデルの内容で在庫数を更新します。
     *
     * @param capacity 総定員
     * @param available 予約可能数
     */
    public void update(int capacity, int available) {
        if (capacity < 0 || available < 0 || available > capacity) {
            throw new IllegalArgumentException("Invalid inventory values");
        }
        this.capacity = capacity;
        this.available = available;
    }
}
