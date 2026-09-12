package com.verne.reservation.domain.exception;

import com.verne.reservation.domain.model.ReservationStatus;

public final class InvalidReservationStateException extends DomainException {

    public InvalidReservationStateException(ReservationStatus currentStatus, String operation) {
        super("Cannot %s a reservation in %s status".formatted(operation, currentStatus));
    }
}
