package com.verne.reservation.application.service;

import com.verne.reservation.application.command.ConfirmReservationCommand;
import com.verne.reservation.application.exception.ResourceNotFoundException;
import com.verne.reservation.application.port.in.ConfirmReservationUseCase;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.application.result.ReservationResult;
import com.verne.reservation.domain.event.ReservationConfirmedEvent;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * 予約確定ユースケースを実行するアプリケーションサービスです。
 */
@Service
@Transactional
public class ConfirmReservationService implements ConfirmReservationUseCase {

    /** 予約リポジトリです。 */
    private final ReservationRepository reservationRepository;

    /** ドメインイベント発行ポートです。 */
    private final DomainEventPublisher eventPublisher;

    /** 現在日時を取得するクロックです。 */
    private final Clock clock;

    /**
     * 予約確定に必要な依存関係を設定します。
     *
     * @param reservationRepository 予約リポジトリ
     * @param eventPublisher イベント発行ポート
     * @param clock 現在日時を取得するクロック
     */
    public ConfirmReservationService(
            ReservationRepository reservationRepository,
            DomainEventPublisher eventPublisher,
            Clock clock
    ) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * 仮予約を確定して予約確定イベントを発行します。
     *
     * @param command 予約確定コマンド
     * @return 確定された予約
     */
    @Override
    public ReservationResult execute(ConfirmReservationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = clock.instant();
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation",
                        command.reservationId()
                ));

        reservation.confirm(now);
        Reservation savedReservation = reservationRepository.save(reservation);
        eventPublisher.publish(new ReservationConfirmedEvent(
                savedReservation.id(),
                savedReservation.userId(),
                savedReservation.eventId(),
                savedReservation.quantity(),
                now
        ));
        return ReservationResult.from(savedReservation);
    }
}
