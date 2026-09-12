package com.verne.reservation.domain.exception;

/**
 * Indicates that an event is not accepting reservations at the requested time.
 */
public final class ReservationWindowClosedException extends DomainException {

    public ReservationWindowClosedException() {
        super("The event is not open for reservations");
    }
}
