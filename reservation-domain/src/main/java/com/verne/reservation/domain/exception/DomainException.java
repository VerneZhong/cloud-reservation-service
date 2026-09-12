package com.verne.reservation.domain.exception;

/**
 * Base exception for violations of reservation domain rules.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
