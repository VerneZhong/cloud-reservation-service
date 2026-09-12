package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservableEvent;

import java.util.Optional;

/**
 * 予約対象イベントに必要な永続化操作を定義するリポジトリです。
 */
public interface ReservableEventRepository {

    Optional<ReservableEvent> findById(EventId id);

    ReservableEvent save(ReservableEvent event);
}
