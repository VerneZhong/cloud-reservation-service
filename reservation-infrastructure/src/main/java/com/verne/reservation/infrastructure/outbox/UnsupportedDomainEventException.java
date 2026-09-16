package com.verne.reservation.infrastructure.outbox;

/**
 * Outbox変換に対応していないドメインイベントを受け取った場合の例外です。
 */
public final class UnsupportedDomainEventException extends RuntimeException {

    /**
     * 対応していないイベント型から例外を生成します。
     *
     * @param eventType イベント型
     */
    public UnsupportedDomainEventException(Class<?> eventType) {
        super("Unsupported domain event: " + eventType.getName());
    }
}
