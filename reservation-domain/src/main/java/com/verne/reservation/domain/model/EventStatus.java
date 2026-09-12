package com.verne.reservation.domain.model;

/**
 * 予約対象イベントのライフサイクル状態を定義します。
 */
public enum EventStatus {

    /** 作成中で、まだ公開されていない状態です。 */
    DRAFT,

    /** 公開済みで、予約受付が可能な状態です。 */
    PUBLISHED,

    /** 予約受付を終了した状態です。 */
    CLOSED,

    /** イベントが中止された状態です。 */
    CANCELLED
}
