package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InsufficientInventoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 在庫の確保および返却に関するビジネスルールを検証します。
 */
class InventoryTest {

    /**
     * 予約可能な在庫が正しく確保されることを確認します。
     */
    @Test
    void reservesAvailableInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);

        inventory.reserve(3);

        assertThat(inventory.available()).isEqualTo(7);
        assertThat(inventory.reserved()).isEqualTo(3);
    }

    /**
     * 残数と同じ数量を確保した場合に在庫がゼロになることを確認します。
     */
    @Test
    void reservesAllAvailableInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 2);

        inventory.reserve(2);

        assertThat(inventory.available()).isZero();
    }

    /**
     * 在庫不足の場合に予約が拒否されることを確認します。
     */
    @Test
    void rejectsReservationWhenInventoryIsInsufficient() {
        Inventory inventory = new Inventory(EventId.newId(), 2);

        assertThatThrownBy(() -> inventory.reserve(3))
                .isInstanceOf(InsufficientInventoryException.class)
                .hasMessageContaining("requested=3")
                .hasMessageContaining("available=2");
    }

    /**
     * ゼロ以下の数量を確保できないことを確認します。
     */
    @Test
    void rejectsNonPositiveReservationQuantity() {
        Inventory inventory = new Inventory(EventId.newId(), 10);

        assertThatThrownBy(() -> inventory.reserve(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be greater than zero");
    }

    /**
     * 確保済み在庫が正しく返却されることを確認します。
     */
    @Test
    void releasesReservedInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);
        inventory.reserve(4);

        inventory.release(2);

        assertThat(inventory.available()).isEqualTo(8);
    }

    /**
     * 総定員を超える在庫返却が拒否されることを確認します。
     */
    @Test
    void rejectsReleaseBeyondCapacity() {
        Inventory inventory = new Inventory(EventId.newId(), 10);

        assertThatThrownBy(() -> inventory.release(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Released quantity exceeds inventory capacity");
    }

    /**
     * 負の総定員で在庫を生成できないことを確認します。
     */
    @Test
    void rejectsNegativeCapacity() {
        assertThatThrownBy(() -> new Inventory(EventId.newId(), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("capacity must not be negative");
    }

    /**
     * 予約可能数が総定員を超える復元データを拒否することを確認します。
     */
    @Test
    void rejectsAvailableInventoryBeyondCapacity() {
        assertThatThrownBy(() -> new Inventory(EventId.newId(), 5, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("available must be between 0 and capacity");
    }
}
