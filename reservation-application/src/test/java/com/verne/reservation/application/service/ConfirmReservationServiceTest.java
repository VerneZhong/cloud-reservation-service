package com.verne.reservation.application.service;

import com.verne.reservation.application.command.ConfirmReservationCommand;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationConfirmedEvent;
import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.ReservationStatus;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 予約確定アプリケーションサービスの処理を検証します。
 */
class ConfirmReservationServiceTest {

    /** テストで使用する固定日時です。 */
    private static final Instant NOW = Instant.parse("2026-09-14T03:00:00Z");

    /**
     * 有効期限内の仮予約を確定できることを確認します。
     */
    @Test
    void confirmsPendingReservation() {
        ReservationRepository repository = mock(ReservationRepository.class);
        DomainEventPublisher publisher = mock(DomainEventPublisher.class);
        Reservation reservation = pendingReservation(NOW.plus(15, ChronoUnit.MINUTES));
        when(repository.findById(reservation.id())).thenReturn(Optional.of(reservation));
        when(repository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ConfirmReservationService service = new ConfirmReservationService(
                repository,
                publisher,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );

        ReservationResult result =
                service.execute(new ConfirmReservationCommand(reservation.id()));

        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.confirmedAt()).isEqualTo(NOW);
        verify(publisher).publish(any(ReservationConfirmedEvent.class));
    }

    /**
     * テスト用の仮予約を生成します。
     *
     * @param expiresAt 有効期限
     * @return 仮予約
     */
    private Reservation pendingReservation(Instant expiresAt) {
        return Reservation.createPending(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                1,
                NOW.minus(5, ChronoUnit.MINUTES),
                expiresAt
        );
    }
}
