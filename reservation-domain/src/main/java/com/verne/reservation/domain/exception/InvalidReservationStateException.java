package com.verne.reservation.domain.exception;

import com.verne.reservation.domain.model.ReservationStatus;

/**
 * 現在の予約状態では許可されていない操作を表す例外です。
 */
public final class InvalidReservationStateException extends DomainException {

    public InvalidReservationStateException(ReservationStatus currentStatus, String operation) {
        super("Cannot %s a reservation in %s status".formatted(operation, currentStatus));
    }
}
