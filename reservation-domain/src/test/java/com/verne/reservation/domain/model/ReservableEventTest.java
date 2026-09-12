package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.ReservationWindowClosedException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies event publication and reservation-window business rules.
 */
class ReservableEventTest {

    private static final Instant NOW = Instant.parse("2026-09-12T10:00:00Z");

    @Test
    void publishedEventIsReservableInsideBookingWindow() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(1, ChronoUnit.DAYS))).isTrue();
    }

    @Test
    void draftEventIsNotReservable() {
        ReservableEvent event = draftEvent();

        assertThatThrownBy(() -> event.ensureReservableAt(NOW.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(ReservationWindowClosedException.class);
    }

    @Test
    void eventIsNotReservableAtClosingTime() {
        ReservableEvent event = draftEvent();
        event.publish();

        assertThat(event.isReservableAt(NOW.plus(7, ChronoUnit.DAYS))).isFalse();
    }

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
