package com.verne.reservation.application.command;

import com.verne.reservation.domain.model.ReservationId;

import java.util.Objects;

/**
 * 予約確定ユースケースへの入力値です。
 *
 * @param reservationId 予約ID
 */
public record ConfirmReservationCommand(ReservationId reservationId) {

    /**
     * 入力値を検証して予約確定コマンドを生成します。
     */
    public ConfirmReservationCommand {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
    }
}
