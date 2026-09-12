package com.verne.reservation.domain.model;

/**
 * 予約のライフサイクル状態を定義します。
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
