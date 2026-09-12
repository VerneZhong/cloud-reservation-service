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

    /**
     * 予約IDで予約を取得します。
     *
     * @param id 予約ID
     * @return 予約。存在しない場合はempty
     */
    Optional<Reservation> findById(ReservationId id);

    /**
     * 利用者IDに紐づく予約一覧を取得します。
     *
     * @param userId 利用者ID
     * @return 予約一覧
     */
    List<Reservation> findByUserId(UserId userId);

    /**
     * 予約を保存します。
     *
     * @param reservation 保存対象の予約
     * @return 保存された予約
     */
    Reservation save(Reservation reservation);
}
