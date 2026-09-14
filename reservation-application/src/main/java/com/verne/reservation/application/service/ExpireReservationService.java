package com.verne.reservation.application.service;

import com.verne.reservation.application.command.ExpireReservationCommand;
import com.verne.reservation.application.exception.ResourceNotFoundException;
import com.verne.reservation.application.port.in.ExpireReservationUseCase;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationExpiredEvent;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.repository.InventoryRepository;
import com.verne.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * 予約失効ユースケースを実行するアプリケーションサービスです。
 */
@Service
@Transactional
public class ExpireReservationService implements ExpireReservationUseCase {

    /** 予約リポジトリです。 */
    private final ReservationRepository reservationRepository;

    /** 在庫リポジトリです。 */
    private final InventoryRepository inventoryRepository;

    /** ドメインイベント発行ポートです。 */
    private final DomainEventPublisher eventPublisher;

    /** 現在日時を取得するクロックです。 */
    private final Clock clock;

    /**
     * 予約失効に必要な依存関係を設定します。
     *
     * @param reservationRepository 予約リポジトリ
     * @param inventoryRepository 在庫リポジトリ
     * @param eventPublisher イベント発行ポート
     * @param clock 現在日時を取得するクロック
     */
    public ExpireReservationService(
            ReservationRepository reservationRepository,
            InventoryRepository inventoryRepository,
            DomainEventPublisher eventPublisher,
            Clock clock
    ) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository);
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * 有効期限を迎えた仮予約を失効させ、在庫を返却します。
     *
     * @param command 予約失効コマンド
     * @return 失効した予約
     */
    @Override
    public ReservationResult execute(ExpireReservationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = clock.instant();
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation",
                        command.reservationId()
                ));
        Inventory inventory = inventoryRepository.findByEventId(reservation.eventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory",
                        reservation.eventId()
                ));

        reservation.expire(now);
        inventory.release(reservation.quantity());

        inventoryRepository.save(inventory);
        Reservation savedReservation = reservationRepository.save(reservation);
        eventPublisher.publish(new ReservationExpiredEvent(
                savedReservation.id(),
                savedReservation.userId(),
                savedReservation.eventId(),
                savedReservation.quantity(),
                now
        ));
        return ReservationResult.from(savedReservation);
    }
}
