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

    /**
     * 有効期限内に仮予約を確定できることを確認します。
     */
    @Test
    void confirmsPendingReservationBeforeExpiration() {
        Reservation reservation = pendingReservation();

        reservation.confirm(NOW.plus(5, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.confirmedAt()).isEqualTo(NOW.plus(5, ChronoUnit.MINUTES));
    }

    /**
     * 有効期限ちょうどの確定が拒否されることを確認します。
     */
    @Test
    void rejectsConfirmationAtExpirationTime() {
        Reservation reservation = pendingReservation();

        assertThatThrownBy(() -> reservation.confirm(NOW.plus(15, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);

        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
    }

    /**
     * 確定済み予約の再確定が拒否されることを確認します。
     */
    @Test
    void rejectsDuplicateConfirmation() {
        Reservation reservation = pendingReservation();
        reservation.confirm(NOW.plus(5, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> reservation.confirm(NOW.plus(6, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    /**
     * 仮予約を取り消せることを確認します。
     */
    @Test
    void cancelsPendingReservation() {
        Reservation reservation = pendingReservation();

        reservation.cancel(NOW.plus(5, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(reservation.cancelledAt()).isEqualTo(NOW.plus(5, ChronoUnit.MINUTES));
    }

    /**
     * 取消済み予約の再取消が拒否されることを確認します。
     */
    @Test
    void rejectsDuplicateCancellation() {
        Reservation reservation = pendingReservation();
        reservation.cancel(NOW.plus(5, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> reservation.cancel(NOW.plus(6, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    /**
     * 有効期限を迎えた仮予約が失効することを確認します。
     */
    @Test
    void expiresPendingReservationAtExpirationTime() {
        Reservation reservation = pendingReservation();

        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThat(reservation.status()).isEqualTo(ReservationStatus.EXPIRED);
    }

    /**
     * 有効期限前の失効処理が拒否されることを確認します。
     */
    @Test
    void rejectsExpirationBeforeDeadline() {
        Reservation reservation = pendingReservation();

        assertThatThrownBy(() -> reservation.expire(NOW.plus(14, ChronoUnit.MINUTES)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Reservation has not reached its expiration time");
    }

    /**
     * 失効済み予約の取消が拒否されることを確認します。
     */
    @Test
    void rejectsCancellationOfExpiredReservation() {
        Reservation reservation = pendingReservation();
        reservation.expire(NOW.plus(15, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> reservation.cancel(NOW.plus(16, ChronoUnit.MINUTES)))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    /**
     * ゼロ以下の予約数で仮予約を生成できないことを確認します。
     */
    @Test
    void rejectsNonPositiveReservationQuantity() {
        assertThatThrownBy(() -> Reservation.createPending(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                0,
                NOW,
                NOW.plus(15, ChronoUnit.MINUTES)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be greater than zero");
    }

    /**
     * 作成日時以前の有効期限が拒否されることを確認します。
     */
    @Test
    void rejectsExpirationNotAfterCreationTime() {
        assertThatThrownBy(() -> Reservation.createPending(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                1,
                NOW,
                NOW
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expiresAt must be after createdAt");
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
