package com.verne.reservation.application.service;

import com.verne.reservation.application.command.CreateReservationCommand;
import com.verne.reservation.application.exception.ResourceNotFoundException;
import com.verne.reservation.application.port.in.CreateReservationUseCase;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationCreatedEvent;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.model.ReservableEvent;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.repository.InventoryRepository;
import com.verne.reservation.domain.repository.ReservableEventRepository;
import com.verne.reservation.domain.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * 予約作成ユースケースを実行するアプリケーションサービスです。
 */
@Service
@Transactional
public class CreateReservationService implements CreateReservationUseCase {

    /** 予約対象イベントのリポジトリです。 */
    private final ReservableEventRepository eventRepository;

    /** 在庫リポジトリです。 */
    private final InventoryRepository inventoryRepository;

    /** 予約リポジトリです。 */
    private final ReservationRepository reservationRepository;

    /** ドメインイベント発行ポートです。 */
    private final DomainEventPublisher eventPublisher;

    /** 現在日時を取得するクロックです。 */
    private final Clock clock;

    /** 仮予約を確定できる期間です。 */
    private final Duration pendingTimeout;

    /**
     * 予約作成に必要な依存関係を設定します。
     *
     * @param eventRepository イベントリポジトリ
     * @param inventoryRepository 在庫リポジトリ
     * @param reservationRepository 予約リポジトリ
     * @param eventPublisher イベント発行ポート
     * @param clock 現在日時を取得するクロック
     * @param pendingTimeout 仮予約の有効期間
     */
    public CreateReservationService(
            ReservableEventRepository eventRepository,
            InventoryRepository inventoryRepository,
            ReservationRepository reservationRepository,
            DomainEventPublisher eventPublisher,
            Clock clock,
            @Value("${app.reservation.pending-timeout:15m}") Duration pendingTimeout
    ) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
        this.reservationRepository = Objects.requireNonNull(reservationRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clock = Objects.requireNonNull(clock);
        this.pendingTimeout = Objects.requireNonNull(pendingTimeout);
        if (pendingTimeout.isZero() || pendingTimeout.isNegative()) {
            throw new IllegalArgumentException("pendingTimeout must be positive");
        }
    }

    /**
     * イベントと在庫を検証し、仮予約を作成します。
     *
     * @param command 予約作成コマンド
     * @return 作成された仮予約
     */
    @Override
    public ReservationResult execute(CreateReservationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = clock.instant();

        ReservableEvent event = eventRepository.findById(command.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", command.eventId()));
        event.ensureReservableAt(now);

        Inventory inventory = inventoryRepository.findByEventId(command.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", command.eventId()));
        inventory.reserve(command.quantity());

        Reservation reservation = Reservation.createPending(
                ReservationId.newId(),
                command.userId(),
                command.eventId(),
                command.quantity(),
                now,
                now.plus(pendingTimeout)
        );

        inventoryRepository.save(inventory);
        Reservation savedReservation = reservationRepository.save(reservation);
        eventPublisher.publish(new ReservationCreatedEvent(
                savedReservation.id(),
                savedReservation.userId(),
                savedReservation.eventId(),
                savedReservation.quantity(),
                now
        ));
        return ReservationResult.from(savedReservation);
    }
}
