package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;

import java.util.Optional;

/**
 * 在庫ドメインに必要な永続化操作を定義するリポジトリです。
 */
public interface InventoryRepository {

    /**
     * イベントIDに対応する在庫を取得します。
     *
     * @param eventId イベントID
     * @return 在庫。存在しない場合はempty
     */
    Optional<Inventory> findByEventId(EventId eventId);

    /**
     * 在庫を保存します。
     *
     * @param inventory 保存対象の在庫
     * @return 保存された在庫
     */
    Inventory save(Inventory inventory);
}
