package com.verne.reservation.application.service;

import com.verne.reservation.application.command.CreateReservationCommand;
import com.verne.reservation.application.exception.ResourceNotFoundException;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationCreatedEvent;
import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.EventStatus;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.model.ReservableEvent;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationStatus;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.domain.repository.InventoryRepository;
import com.verne.reservation.domain.repository.ReservableEventRepository;
import com.verne.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 予約作成アプリケーションサービスの処理を検証します。
 */
@ExtendWith(MockitoExtension.class)
class CreateReservationServiceTest {

    /** テストで使用する固定日時です。 */
    private static final Instant NOW = Instant.parse("2026-09-14T03:00:00Z");

    /** イベントリポジトリのモックです。 */
    @Mock
    private ReservableEventRepository eventRepository;

    /** 在庫リポジトリのモックです。 */
    @Mock
    private InventoryRepository inventoryRepository;

    /** 予約リポジトリのモックです。 */
    @Mock
    private ReservationRepository reservationRepository;

    /** イベント発行ポートのモックです。 */
    @Mock
    private DomainEventPublisher eventPublisher;

    /** テスト対象のサービスです。 */
    private CreateReservationService service;

    /**
     * 各テストで使用するサービスを初期化します。
     */
    @BeforeEach
    void setUp() {
        service = new CreateReservationService(
                eventRepository,
                inventoryRepository,
                reservationRepository,
                eventPublisher,
                Clock.fixed(NOW, ZoneOffset.UTC),
                Duration.ofMinutes(15)
        );
    }

    /**
     * イベントと在庫が有効な場合に仮予約を作成できることを確認します。
     */
    @Test
    void createsPendingReservation() {
        EventId eventId = EventId.newId();
        UserId userId = UserId.newId();
        ReservableEvent event = publishedEvent(eventId);
        Inventory inventory = new Inventory(eventId, 10);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(inventoryRepository.findByEventId(eventId)).thenReturn(Optional.of(inventory));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResult result =
                service.execute(new CreateReservationCommand(userId, eventId, 2));

        assertThat(result.status()).isEqualTo(ReservationStatus.PENDING);
        assertThat(result.quantity()).isEqualTo(2);
        assertThat(result.expiresAt()).isEqualTo(NOW.plus(15, ChronoUnit.MINUTES));
        assertThat(inventory.available()).isEqualTo(8);
        verify(inventoryRepository).save(inventory);
        verify(eventPublisher).publish(any(ReservationCreatedEvent.class));
    }

    /**
     * 対象イベントが存在しない場合に処理を中止することを確認します。
     */
    @Test
    void rejectsRequestWhenEventDoesNotExist() {
        EventId eventId = EventId.newId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(
                new CreateReservationCommand(UserId.newId(), eventId, 1)
        )).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found");

        verify(inventoryRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    /**
     * テスト用の公開済みイベントを生成します。
     *
     * @param eventId イベントID
     * @return 公開済みイベント
     */
    private ReservableEvent publishedEvent(EventId eventId) {
        return new ReservableEvent(
                eventId,
                "AWS Workshop",
                "Cloud-native reservation workshop",
                NOW.plus(10, ChronoUnit.DAYS),
                NOW.minus(1, ChronoUnit.DAYS),
                NOW.plus(7, ChronoUnit.DAYS),
                EventStatus.PUBLISHED
        );
    }
}
