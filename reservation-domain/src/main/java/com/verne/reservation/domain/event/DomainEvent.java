package com.verne.reservation.domain.event;

import java.time.Instant;

/**
 * 予約ドメインで発生するイベントの共通インターフェースです。
 */
public interface DomainEvent {

    Instant occurredAt();
}
