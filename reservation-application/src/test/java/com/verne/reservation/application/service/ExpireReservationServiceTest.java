package com.verne.reservation.application.service;

import com.verne.reservation.application.command.ExpireReservationCommand;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationExpiredEvent;
import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.ReservationStatus;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.domain.repository.InventoryRepository;
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
 * 予約失効アプリケーションサービスの処理を検証します。
 */
class ExpireReservationServiceTest {

    /** テストで使用する固定日時です。 */
    private static final Instant NOW = Instant.parse("2026-09-14T03:00:00Z");

    /**
     * 期限切れ予約の失効時に在庫が返却され、イベントが発行されることを確認します。
     */
    @Test
    void expiresReservationAndReleasesInventory() {
        ReservationRepository reservationRepository = mock(ReservationRepository.class);
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        DomainEventPublisher publisher = mock(DomainEventPublisher.class);
        EventId eventId = EventId.newId();
        Reservation reservation = Reservation.createPending(
                ReservationId.newId(),
                UserId.newId(),
                eventId,
                2,
                NOW.minus(15, ChronoUnit.MINUTES),
                NOW
        );
        Inventory inventory = new Inventory(eventId, 10, 8);
        when(reservationRepository.findById(reservation.id()))
                .thenReturn(Optional.of(reservation));
        when(inventoryRepository.findByEventId(eventId)).thenReturn(Optional.of(inventory));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ExpireReservationService service = new ExpireReservationService(
                reservationRepository,
                inventoryRepository,
                publisher,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );

        ReservationResult result =
                service.execute(new ExpireReservationCommand(reservation.id()));

        assertThat(result.status()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(inventory.available()).isEqualTo(10);
        verify(inventoryRepository).save(inventory);
        verify(publisher).publish(any(ReservationExpiredEvent.class));
    }
}
