package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InvalidReservationStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 予約のライフサイクルにおける状態遷移を検証します。
 */
class ReservationTest {

    /**
     * テストで基準とする固定日時です。
     */
    private static final Instant NOW = Instant.parse("2026-09-12T10:00:00Z");

    @Test
    /**
     * 有効期限内に仮予約を確定できることを確認します。
     */
    void confirmsPendingReservationBeforeExpiration() {
        Reservation reservation = pendingReservation();

        reservation.confirm(NOW.plus(5, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.confirmedAt()).isEqualTo(NOW.plus(5, ChronoUnit.MINUTES));
    }

    @Test
    /**
     * 有効期限後の確定が拒否されることを確認します。
     */
    void rejectsConfirmationAfterExpiration() {
        Reservation reservation = pendingReservation();

        assertThatThrownBy(() -> reservation.confirm(NOW.plus(15, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);

        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    /**
     * 有効期限を迎えた仮予約が失効することを確認します。
     */
    void expiresPendingReservationAtExpirationTime() {
        Reservation reservation = pendingReservation();

        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    /**
     * 失効済み予約の取消が拒否されることを確認します。
     */
    void rejectsCancellationOfExpiredReservation() {
        Reservation reservation = pendingReservation();
        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> reservation.cancel(NOW.plus(16, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    /**
     * テスト用の仮予約を生成します。
     *
     * @return テスト用の仮予約
     */
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
