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

    /**
     * 公開済みイベントが受付期間内に予約可能であることを確認します。
     */
    @Test
    void publishedEventIsReservableInsideBookingWindow() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(1, ChronoUnit.DAYS))).isTrue();
    }

    /**
     * 受付開始日時ちょうどに予約可能であることを確認します。
     */
    @Test
    void eventIsReservableAtOpeningTime() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW)).isTrue();
    }

    /**
     * 受付終了日時ちょうどから予約不可になることを確認します。
     */
    @Test
    void eventIsNotReservableAtClosingTime() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(7, ChronoUnit.DAYS))).isFalse();
    }

    /**
     * 下書きイベントが予約不可であることを確認します。
     */
    @Test
    void draftEventIsNotReservable() {
        ReservableEvent event = draftEvent();

        assertThatThrownBy(() -> event.ensureReservableAt(NOW.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(ReservationWindowClosedException.class);
    }

    /**
     * 公開済みイベントを再度公開できないことを確認します。
     */
    @Test
    void rejectsPublishingAnAlreadyPublishedEvent() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThatThrownBy(event::publish)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only a draft event can be published");
    }

    /**
     * 下書きイベントの受付を終了できないことを確認します。
     */
    @Test
    void rejectsClosingDraftEvent() {
        ReservableEvent event = draftEvent();

        assertThatThrownBy(event::close)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only a published event can be closed");
    }

    /**
     * 中止操作を繰り返しても中止状態が維持されることを確認します。
     */
    @Test
    void cancellingEventIsIdempotent() {
        ReservableEvent event = draftEvent();

        event.cancel();
        event.cancel();

        assertThat(event.status()).isEqualTo(EventStatus.CANCELLED);
    }

    /**
     * 空のイベント名が拒否されることを確認します。
     */
    @Test
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> new ReservableEvent(
                EventId.newId(),
                " ",
                "",
                NOW.plus(10, ChronoUnit.DAYS),
                NOW,
                NOW.plus(7, ChronoUnit.DAYS),
                EventStatus.DRAFT
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
    }

    /**
     * 受付開始日時が終了日時以降の場合に生成が拒否されることを確認します。
     */
    @Test
    void rejectsInvalidBookingWindow() {
        assertThatThrownBy(() -> new ReservableEvent(
                EventId.newId(),
                "Spring Boot Workshop",
                "",
                NOW.plus(10, ChronoUnit.DAYS),
                NOW.plus(7, ChronoUnit.DAYS),
                NOW.plus(7, ChronoUnit.DAYS),
                EventStatus.DRAFT
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bookingOpensAt must be before bookingClosesAt");
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
