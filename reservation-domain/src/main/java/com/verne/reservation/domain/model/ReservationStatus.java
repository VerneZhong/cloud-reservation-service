package com.verne.reservation.domain.model;

/**
 * 予約のライフサイクル状態を定義します。
 */
public enum ReservationStatus {

    /** 確定待ちの仮予約状態です。 */
    PENDING,

    /** 予約が確定した状態です。 */
    CONFIRMED,

    /** 予約が取り消された状態です。 */
    CANCELLED,

    /** 確定期限を過ぎて失効した状態です。 */
    EXPIRED
}
