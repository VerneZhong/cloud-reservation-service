package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InvalidReservationStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private static final Instant NOW = Instant.parse("2026-09-12T10:00:00Z");

    @Test
    void confirmsPendingReservationBeforeExpiration() {
        Reservation reservation = pendingReservation();

        reservation.confirm(NOW.plus(5, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.confirmedAt()).isEqualTo(NOW.plus(5, ChronoUnit.MINUTES));
    }

    @Test
    void rejectsConfirmationAfterExpiration() {
        Reservation reservation = pendingReservation();

        assertThatThrownBy(() -> reservation.confirm(NOW.plus(15, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);

        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void expiresPendingReservationAtExpirationTime() {
        Reservation reservation = pendingReservation();

        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void rejectsCancellationOfExpiredReservation() {
        Reservation reservation = pendingReservation();
        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> reservation.cancel(NOW.plus(16, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    private Reservation pendingReservation() {
        return Reservation.createPending(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                2,
                NOW,
                NOW.plus(15, ChronoUnit.MINUTES)
        );
    }
}
