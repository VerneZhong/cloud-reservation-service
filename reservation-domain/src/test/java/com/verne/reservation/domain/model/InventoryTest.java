package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.InsufficientInventoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies inventory reservation and release business rules.
 */
class InventoryTest {

    @Test
    void reservesAvailableInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);

        inventory.reserve(3);

        assertThat(inventory.available()).isEqualTo(7);
        assertThat(inventory.reserved()).isEqualTo(3);
    }

    @Test
    void rejectsReservationWhenInventoryIsInsufficient() {
        Inventory inventory = new Inventory(EventId.newId(), 2);

        assertThatThrownBy(() -> inventory.reserve(3))
                .isInstanceOf(InsufficientInventoryException.class)
                .hasMessageContaining("requested=3")
                .hasMessageContaining("available=2");
    }

    @Test
    void releasesReservedInventory() {
        Inventory inventory = new Inventory(EventId.newId(), 10);
        inventory.reserve(4);

        inventory.release(2);

        assertThat(inventory.available()).isEqualTo(8);
    }
}
