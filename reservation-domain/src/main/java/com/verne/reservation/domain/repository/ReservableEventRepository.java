package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservableEvent;

import java.util.Optional;

/**
 * Defines persistence operations required by the reservable event domain.
 */
public interface ReservableEventRepository {

    Optional<ReservableEvent> findById(EventId id);

    ReservableEvent save(ReservableEvent event);
}
