package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.ReservationWindowClosedException;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate that manages event details, publication state, and reservation window.
 */
public final class ReservableEvent {

    private final EventId id;
    private final String title;
    private final String description;
    private final Instant startsAt;
    private final Instant bookingOpensAt;
    private final Instant bookingClosesAt;
    private EventStatus status;

    public ReservableEvent(
            EventId id,
            String title,
            String description,
            Instant startsAt,
            Instant bookingOpensAt,
            Instant bookingClosesAt,
            EventStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = requireText(title, "title");
        this.description = Objects.requireNonNullElse(description, "");
        this.startsAt = Objects.requireNonNull(startsAt, "startsAt must not be null");
        this.bookingOpensAt = Objects.requireNonNull(bookingOpensAt, "bookingOpensAt must not be null");
        this.bookingClosesAt = Objects.requireNonNull(bookingClosesAt, "bookingClosesAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");

        if (!bookingOpensAt.isBefore(bookingClosesAt)) {
            throw new IllegalArgumentException("bookingOpensAt must be before bookingClosesAt");
        }
        if (bookingClosesAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("bookingClosesAt must not be after startsAt");
        }
    }

    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException("Only a draft event can be published");
        }
        status = EventStatus.PUBLISHED;
    }

    public void close() {
        if (status != EventStatus.PUBLISHED) {
            throw new IllegalStateException("Only a published event can be closed");
        }
        status = EventStatus.CLOSED;
    }

    public void cancel() {
        if (status == EventStatus.CANCELLED) {
            return;
        }
        status = EventStatus.CANCELLED;
    }

    public void ensureReservableAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (status != EventStatus.PUBLISHED
                || now.isBefore(bookingOpensAt)
                || !now.isBefore(bookingClosesAt)) {
            throw new ReservationWindowClosedException();
        }
    }

    public boolean isReservableAt(Instant now) {
        try {
            ensureReservableAt(now);
            return true;
        } catch (ReservationWindowClosedException exception) {
            return false;
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    public EventId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public Instant startsAt() {
        return startsAt;
    }

    public Instant bookingOpensAt() {
        return bookingOpensAt;
    }

    public Instant bookingClosesAt() {
        return bookingClosesAt;
    }

    public EventStatus status() {
        return status;
    }
}
