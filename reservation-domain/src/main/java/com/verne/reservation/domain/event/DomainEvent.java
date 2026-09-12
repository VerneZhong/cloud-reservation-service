package com.verne.reservation.domain.event;

import java.time.Instant;

/**
 * 予約ドメインで発生するイベントの共通インターフェースです。
 */
public interface DomainEvent {

    /**
     * イベントが発生した日時を返します。
     *
     * @return イベント発生日時
     */
    Instant occurredAt();
}
