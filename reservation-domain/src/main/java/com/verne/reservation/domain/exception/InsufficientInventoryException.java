package com.verne.reservation.domain.exception;

/**
 * Indicates that the requested quantity exceeds the available inventory.
 */
public final class InsufficientInventoryException extends DomainException {

    public InsufficientInventoryException(int requested, int available) {
        super("Insufficient inventory: requested=%d, available=%d".formatted(requested, available));
    }
}
