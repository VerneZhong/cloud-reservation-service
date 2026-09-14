package com.verne.reservation.application.port.in;

import com.verne.reservation.application.command.ConfirmReservationCommand;
import com.verne.reservation.application.result.ReservationResult;

/**
 * 予約確定処理の入力ポートです。
 */
public interface ConfirmReservationUseCase {

    /**
     * 仮予約を確定します。
     *
     * @param command 予約確定コマンド
     * @return 確定された予約
     */
    ReservationResult execute(ConfirmReservationCommand command);
}
