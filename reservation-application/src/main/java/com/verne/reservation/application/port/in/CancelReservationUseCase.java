package com.verne.reservation.application.port.in;

import com.verne.reservation.application.command.CancelReservationCommand;
import com.verne.reservation.application.result.ReservationResult;

/**
 * 予約取消処理の入力ポートです。
 */
public interface CancelReservationUseCase {

    /**
     * 予約を取り消して在庫を返却します。
     *
     * @param command 予約取消コマンド
     * @return 取り消された予約
     */
    ReservationResult execute(CancelReservationCommand command);
}
