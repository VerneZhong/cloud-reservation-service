package com.verne.reservation.application.command;

import com.verne.reservation.domain.model.ReservationId;

import java.util.Objects;

/**
 * 予約失効ユースケースへの入力値です。
 *
 * @param reservationId 予約ID
 */
public record ExpireReservationCommand(ReservationId reservationId) {

    /**
     * 入力値を検証して予約失効コマンドを生成します。
     */
    public ExpireReservationCommand {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
    }
}
