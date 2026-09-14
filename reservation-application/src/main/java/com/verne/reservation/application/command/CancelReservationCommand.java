package com.verne.reservation.application.command;

import com.verne.reservation.domain.model.ReservationId;

import java.util.Objects;

/**
 * 予約取消ユースケースへの入力値です。
 *
 * @param reservationId 予約ID
 */
public record CancelReservationCommand(ReservationId reservationId) {

    /**
     * 入力値を検証して予約取消コマンドを生成します。
     */
    public CancelReservationCommand {
        Objects.requireNonNull(reservationId, "reservationId must not be null");
    }
}
