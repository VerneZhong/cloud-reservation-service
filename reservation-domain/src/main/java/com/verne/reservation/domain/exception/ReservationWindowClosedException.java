package com.verne.reservation.domain.exception;

public final class ReservationWindowClosedException extends DomainException {

    public ReservationWindowClosedException() {
        super("The event is not open for reservations");
    }
}
