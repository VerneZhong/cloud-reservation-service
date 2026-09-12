package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InvalidReservationStateException;

import java.time.Instant;
import java.util.Objects;

public final class Reservation {

    private final ReservationId id;
    private final UserId userId;
    private final EventId eventId;
    private final int quantity;
    private final Instant createdAt;
    private final Instant expiresAt;
    private ReservationStatus status;
    private Instant confirmedAt;
    private Instant cancelledAt;

    public Reservation(
            ReservationId id,
            UserId userId,
            EventId eventId,
            int quantity,
            ReservationStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant confirmedAt,
            Instant cancelledAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    public static Reservation createPending(
            ReservationId id,
            UserId userId,
            EventId eventId,
            int quantity,
            Instant createdAt,
            Instant expiresAt
    ) {
        return new Reservation(
                id,
                userId,
                eventId,
                quantity,
                ReservationStatus.PENDING,
                createdAt,
                expiresAt,
                null,
                null
        );
    }

    public void confirm(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        requireStatus(ReservationStatus.PENDING, "confirm");
        if (!now.isBefore(expiresAt)) {
            throw new InvalidReservationStateException(status, "confirm an expired");
        }
        status = ReservationStatus.CONFIRMED;
        confirmedAt = now;
    }

    public void cancel(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (status != ReservationStatus.PENDING && status != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStateException(status, "cancel");
        }
        status = ReservationStatus.CANCELLED;
        cancelledAt = now;
    }

    public void expire(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        requireStatus(ReservationStatus.PENDING, "expire");
        if (now.isBefore(expiresAt)) {
            throw new IllegalStateException("Reservation has not reached its expiration time");
        }
        status = ReservationStatus.EXPIRED;
    }

    private void requireStatus(ReservationStatus requiredStatus, String operation) {
        if (status != requiredStatus) {
            throw new InvalidReservationStateException(status, operation);
        }
    }

    public ReservationId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public EventId eventId() {
        return eventId;
    }

    public int quantity() {
        return quantity;
    }

    public ReservationStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant confirmedAt() {
        return confirmedAt;
    }

    public Instant cancelledAt() {
        return cancelledAt;
    }
}
