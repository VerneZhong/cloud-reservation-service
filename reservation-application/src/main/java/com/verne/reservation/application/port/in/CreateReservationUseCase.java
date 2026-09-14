package com.verne.reservation.application.port.in;

import com.verne.reservation.application.command.CreateReservationCommand;
import com.verne.reservation.application.result.ReservationResult;

/**
 * 予約作成処理の入力ポートです。
 */
public interface CreateReservationUseCase {

    /**
     * イベントの予約を作成します。
     *
     * @param command 予約作成コマンド
     * @return 作成された予約
     */
    ReservationResult execute(CreateReservationCommand command);
}
