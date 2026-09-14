package com.verne.reservation.application.port.out;

import com.verne.reservation.domain.event.DomainEvent;

/**
 * ドメインイベントを外部へ発行する出力ポートです。
 */
public interface DomainEventPublisher {

    /**
     * ドメインイベントを発行します。
     *
     * @param event 発行対象のドメインイベント
     */
    void publish(DomainEvent event);
}
