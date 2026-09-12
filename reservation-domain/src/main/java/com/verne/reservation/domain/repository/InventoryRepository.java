package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;

import java.util.Optional;

/**
 * 在庫ドメインに必要な永続化操作を定義するリポジトリです。
 */
public interface InventoryRepository {

    Optional<Inventory> findByEventId(EventId eventId);

    Inventory save(Inventory inventory);
}
