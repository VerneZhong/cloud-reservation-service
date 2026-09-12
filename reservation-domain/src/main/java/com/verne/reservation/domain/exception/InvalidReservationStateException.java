package com.verne.reservation.domain.exception;

import com.verne.reservation.domain.model.ReservationStatus;

/**
 * 現在の予約状態では許可されていない操作を表す例外です。
 */
public final class InvalidReservationStateException extends DomainException {

    /**
     * 現在の状態と実行しようとした操作を使用して例外を生成します。
     *
     * @param currentStatus 現在の予約状態
     * @param operation 実行しようとした操作
     */
    public InvalidReservationStateException(ReservationStatus currentStatus, String operation) {
        super("Cannot %s a reservation in %s status".formatted(operation, currentStatus));
    }
}
