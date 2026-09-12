package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InsufficientInventoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 在庫の確保および返却に関するビジネスルールを検証します。
 */
class InventoryTest {

    @Test
    /**
     * 予約可能な在庫が正しく確保されることを確認します。
     */
    void reservesAvailableInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);

        inventory.reserve(3);

        assertThat(inventory.available()).isEqualTo(7);
        assertThat(inventory.reserved()).isEqualTo(3);
    }

    @Test
    /**
     * 在庫不足の場合に予約が拒否されることを確認します。
     */
    void rejectsReservationWhenInventoryIsInsufficient() {
        Inventory inventory = new Inventory(EventId.newId(), 2);

        assertThatThrownBy(() -> inventory.reserve(3))
                .isInstanceOf(InsufficientInventoryException.class)
                .hasMessageContaining("requested=3")
                .hasMessageContaining("available=2");
    }

    @Test
    /**
     * 確保済み在庫が正しく返却されることを確認します。
     */
    void releasesReservedInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);
        inventory.reserve(4);

        inventory.release(2);

        assertThat(inventory.available()).isEqualTo(8);
    }
}
