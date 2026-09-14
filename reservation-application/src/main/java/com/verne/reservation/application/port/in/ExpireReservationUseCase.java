package com.verne.reservation.application.port.in;

import com.verne.reservation.application.command.ExpireReservationCommand;
import com.verne.reservation.application.result.ReservationResult;

/**
 * 予約失効処理の入力ポートです。
 */
public interface ExpireReservationUseCase {

    /**
     * 有効期限を迎えた仮予約を失効させて在庫を返却します。
     *
     * @param command 予約失効コマンド
     * @return 失効した予約
     */
    ReservationResult execute(ExpireReservationCommand command);
}
