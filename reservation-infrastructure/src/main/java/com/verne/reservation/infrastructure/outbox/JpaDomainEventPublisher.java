package com.verne.reservation.infrastructure.outbox;

import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.domain.event.DomainEvent;
import com.verne.reservation.domain.event.ReservationCancelledEvent;
import com.verne.reservation.domain.event.ReservationConfirmedEvent;
import com.verne.reservation.domain.event.ReservationCreatedEvent;
import com.verne.reservation.domain.event.ReservationExpiredEvent;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

/**
 * ドメインイベントを同一トランザクション内でOutboxへ保存します。
 */
@Component
public class JpaDomainEventPublisher implements DomainEventPublisher {

    /** Outboxイベントリポジトリです。 */
    private final SpringDataOutboxEventRepository repository;

    /** Outbox登録日時を取得するクロックです。 */
    private final Clock clock;

    /**
     * Outbox保存に必要な依存関係を設定します。
     *
     * @param repository Outboxイベントリポジトリ
     * @param clock UTCクロック
     */
    public JpaDomainEventPublisher(
            SpringDataOutboxEventRepository repository,
            Clock clock
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * ドメインイベントをOutbox形式へ変換して保存します。
     *
     * @param event 発行対象のドメインイベント
     */
    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        EventData data = toEventData(event);
        repository.save(new OutboxEventJpaEntity(
                UUID.randomUUID(),
                event.getClass().getSimpleName(),
                data.reservationId(),
                data.userId(),
                data.eventId(),
                data.quantity(),
                event.occurredAt(),
                clock.instant()
        ));
    }

    /**
     * 対応する予約イベントから共通Outbox項目を取り出します。
     *
     * @param event ドメインイベント
     * @return Outbox保存用データ
     */
    private EventData toEventData(DomainEvent event) {
        return switch (event) {
            case ReservationCreatedEvent value -> new EventData(
                    value.reservationId().value(),
                    value.userId().value(),
                    value.eventId().value(),
                    value.quantity()
            );
            case ReservationConfirmedEvent value -> new EventData(
                    value.reservationId().value(),
                    value.userId().value(),
                    value.eventId().value(),
                    value.quantity()
            );
            case ReservationCancelledEvent value -> new EventData(
                    value.reservationId().value(),
                    value.userId().value(),
                    value.eventId().value(),
                    value.quantity()
            );
            case ReservationExpiredEvent value -> new EventData(
                    value.reservationId().value(),
                    value.userId().value(),
                    value.eventId().value(),
                    value.quantity()
            );
            default -> throw new UnsupportedDomainEventException(event.getClass());
        };
    }

    /**
     * Outboxへ保存する予約イベントの共通項目です。
     *
     * @param reservationId 予約ID
     * @param userId 利用者ID
     * @param eventId イベントID
     * @param quantity 予約数
     */
    private record EventData(
            UUID reservationId,
            UUID userId,
            UUID eventId,
            int quantity
    ) {
    }
}
