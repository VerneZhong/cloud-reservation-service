package com.verne.reservation.domain.event;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 予約に関するドメインイベントの生成条件を検証します。
 */
class ReservationDomainEventTest {

    /**
     * テストで使用するイベント発生日時です。
     */
    private static final Instant OCCURRED_AT = Instant.parse("2026-09-12T10:00:00Z");

    /**
     * 予約確定イベントが必要な情報を保持することを確認します。
     */
    @Test
    void createsReservationConfirmedEvent() {
        ReservationId reservationId = ReservationId.newId();
        UserId userId = UserId.newId();
        EventId eventId = EventId.newId();

        ReservationConfirmedEvent event =
                new ReservationConfirmedEvent(reservationId, userId, eventId, 2, OCCURRED_AT);

        assertThat(event.reservationId()).isEqualTo(reservationId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.quantity()).isEqualTo(2);
        assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    }

    /**
     * 予約取消イベントがゼロ以下の予約数を拒否することを確認します。
     */
    @Test
    void cancelledEventRejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> new ReservationCancelledEvent(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                0,
                OCCURRED_AT
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be greater than zero");
    }

    /**
     * 予約失効イベントがnullの予約IDを拒否することを確認します。
     */
    @Test
    void expiredEventRejectsNullReservationId() {
        assertThatThrownBy(() -> new ReservationExpiredEvent(
                null,
                UserId.newId(),
                EventId.newId(),
                1,
                OCCURRED_AT
        )).isInstanceOf(NullPointerException.class)
                .hasMessage("reservationId must not be null");
    }
}
