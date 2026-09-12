package com.verne.reservation.domain.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;

import java.util.Optional;

/**
 * Defines persistence operations required by the inventory domain.
 */
public interface InventoryRepository {

    Optional<Inventory> findByEventId(EventId eventId);

    Inventory save(Inventory inventory);
}
