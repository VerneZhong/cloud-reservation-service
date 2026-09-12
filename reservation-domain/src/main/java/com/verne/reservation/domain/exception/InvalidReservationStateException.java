package com.verne.reservation.domain.exception;

import com.verne.reservation.domain.model.ReservationStatus;

/**
 * Indicates that an operation is not allowed in the current reservation state.
 */
public final class InvalidReservationStateException extends DomainException {

    public InvalidReservationStateException(ReservationStatus currentStatus, String operation) {
        super("Cannot %s a reservation in %s status".formatted(operation, currentStatus));
    }
}
