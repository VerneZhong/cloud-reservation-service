package com.verne.reservation.domain.model;

/**
 * 予約対象イベントのライフサイクル状態を定義します。
 */
public enum EventStatus {
    DRAFT,
    PUBLISHED,
    CLOSED,
    CANCELLED
}
