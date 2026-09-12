package com.verne.reservation.domain.exception;

/**
 * 指定時刻にイベントの予約を受け付けていない場合に発生する例外です。
 */
public final class ReservationWindowClosedException extends DomainException {

    public ReservationWindowClosedException() {
        super("The event is not open for reservations");
    }
}
