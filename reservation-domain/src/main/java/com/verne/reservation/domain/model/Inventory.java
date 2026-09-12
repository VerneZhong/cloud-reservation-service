package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InsufficientInventoryException;

import java.util.Objects;

/**
 * イベントの定員と予約可能数を管理するドメインモデルです。
 */
public final class Inventory {

    private final EventId eventId;
    private final int capacity;
    private int available;

    public Inventory(EventId eventId, int capacity) {
        this(eventId, capacity, capacity);
    }

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

    public void reserve(int quantity) {
        requirePositive(quantity);
        if (quantity > available) {
            throw new InsufficientInventoryException(quantity, available);
        }
        available -= quantity;
    }

    public void release(int quantity) {
        requirePositive(quantity);
        if (available + quantity > capacity) {
            throw new IllegalStateException("Released quantity exceeds inventory capacity");
        }
        available += quantity;
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }

    public EventId eventId() {
        return eventId;
    }

    public int capacity() {
        return capacity;
    }

    public int available() {
        return available;
    }

    public int reserved() {
        return capacity - available;
    }
}
