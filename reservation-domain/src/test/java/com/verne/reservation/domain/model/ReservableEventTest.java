package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.ReservationWindowClosedException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * イベントの公開状態と予約受付期間に関するルールを検証します。
 */
class ReservableEventTest {

    /**
     * テストで基準とする固定日時です。
     */
    private static final Instant NOW = Instant.parse("2026-09-12T10:00:00Z");

    @Test
    /**
     * 公開済みイベントが受付期間内に予約可能であることを確認します。
     */
    void publishedEventIsReservableInsideBookingWindow() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(1, ChronoUnit.DAYS))).isTrue();
    }

    @Test
    /**
     * 下書きイベントが予約不可であることを確認します。
     */
    void draftEventIsNotReservable() {
        ReservableEvent event = draftEvent();

        assertThatThrownBy(() -> event.ensureReservableAt(NOW.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(ReservationWindowClosedException.class);
    }

    @Test
    /**
     * 受付終了日時以降は予約不可であることを確認します。
     */
    void eventIsNotReservableAtClosingTime() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(7, ChronoUnit.DAYS))).isFalse();
    }

    /**
     * テスト用の下書きイベントを生成します。
     *
     * @return テスト用イベント
     */
    private ReservableEvent draftEvent() {
        return new ReservableEvent(
                EventId.newId(),
                "Spring Boot Workshop",
                "A practical cloud-native Java workshop.",
                NOW.plus(10, ChronoUnit.DAYS),
                NOW,
                NOW.plus(7, ChronoUnit.DAYS),
                EventStatus.DRAFT
        );
    }
}
