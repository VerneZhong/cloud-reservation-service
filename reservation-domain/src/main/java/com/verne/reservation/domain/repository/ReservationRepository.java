package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Defines persistence operations required by the reservation domain.
 */
public interface ReservationRepository {

    Optional<Reservation> findById(ReservationId id);

    List<Reservation> findByUserId(UserId userId);

    Reservation save(Reservation reservation);
}
