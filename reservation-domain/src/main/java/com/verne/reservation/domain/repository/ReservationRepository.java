package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 予約ドメインに必要な永続化操作を定義するリポジトリです。
 */
public interface ReservationRepository {

    Optional<Reservation> findById(ReservationId id);

    List<Reservation> findByUserId(UserId userId);

    Reservation save(Reservation reservation);
}
