package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservableEvent;

import java.util.Optional;

/**
 * 予約対象イベントに必要な永続化操作を定義するリポジトリです。
 */
public interface ReservableEventRepository {

    /**
     * イベントIDで予約対象イベントを取得します。
     *
     * @param id イベントID
     * @return イベント。存在しない場合はempty
     */
    Optional<ReservableEvent> findById(EventId id);

    /**
     * 予約対象イベントを保存します。
     *
     * @param event 保存対象のイベント
     * @return 保存されたイベント
     */
    ReservableEvent save(ReservableEvent event);
}
