package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InsufficientInventoryException;

import java.util.Objects;

/**
 * イベントの定員と予約可能数を管理するドメインモデルです。
 */
public final class Inventory {

    /**
     * 在庫を管理するイベントのIDです。
     */
    private final EventId eventId;
    /**
     * イベントの総定員です。
     */
    private final int capacity;
    /**
     * 現在の予約可能数です。
     */
    private int available;

    /**
     * 指定された総定員をすべて予約可能な状態として在庫を生成します。
     *
     * @param eventId イベントID
     * @param capacity 総定員
     */
    public Inventory(EventId eventId, int capacity) {
        this(eventId, capacity, capacity);
    }

    /**
     * 永続化された値から在庫を復元します。
     *
     * @param eventId イベントID
     * @param capacity 総定員
     * @param available 予約可能数
     */
    public Inventory(EventId eventId, int capacity, int available) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must not be negative");
        }
        if (available < 0 || available > capacity) {
            throw new IllegalArgumentException("available must be between 0 and capacity");
        }
        this.capacity = capacity;
        this.available = available;
    }

    /**
     * 指定数の在庫を確保します。
     *
     * @param quantity 確保する数量
     * @throws com.verne.reservation.domain.exception.InsufficientInventoryException 在庫不足の場合
     */
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (quantity > available) {
            throw new InsufficientInventoryException(quantity, available);
        }
        available -= quantity;
    }

    /**
     * 確保済みの在庫を指定数だけ返却します。
     *
     * @param quantity 返却する数量
     */
    public void release(int quantity) {
        requirePositive(quantity);
        if (available + quantity > capacity) {
            throw new IllegalStateException("Released quantity exceeds inventory capacity");
        }
        available += quantity;
    }

    /**
     * 数量が正の値であることを検証します。
     *
     * @param quantity 検証対象の数量
     */
    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }

    /**
     * 在庫に対応するイベントIDを返します。
     *
     * @return イベントID
     */
    public EventId eventId() {
        return eventId;
    }

    /**
     * 総定員を返します。
     *
     * @return 総定員
     */
    public int capacity() {
        return capacity;
    }

    /**
     * 予約可能数を返します。
     *
     * @return 予約可能数
     */
    public int available() {
        return available;
    }

    /**
     * 予約済み数を返します。
     *
     * @return 予約済み数
     */
    public int reserved() {
        return capacity - available;
    }
}
