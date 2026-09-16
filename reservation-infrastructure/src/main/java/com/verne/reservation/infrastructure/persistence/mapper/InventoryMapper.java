package com.verne.reservation.infrastructure.persistence.mapper;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.infrastructure.persistence.entity.InventoryJpaEntity;

import java.util.Objects;

/**
 * 在庫のドメインモデルとJPAエンティティを相互変換します。
 */
public final class InventoryMapper {

    /**
     * インスタンス化を禁止します。
     */
    private InventoryMapper() {
    }

    /**
     * JPAエンティティをドメインモデルへ変換します。
     *
     * @param entity JPAエンティティ
     * @return 在庫ドメインモデル
     */
    public static Inventory toDomain(InventoryJpaEntity entity) {
        Objects.requireNonNull(entity);
        return new Inventory(
                new EventId(entity.getEventId()),
                entity.getCapacity(),
                entity.getAvailable()
        );
    }

    /**
     * ドメインモデルから新規JPAエンティティを生成します。
     *
     * @param inventory 在庫ドメインモデル
     * @return JPAエンティティ
     */
    public static InventoryJpaEntity toNewEntity(Inventory inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryJpaEntity(
                inventory.eventId().value(),
                inventory.capacity(),
                inventory.available()
        );
    }

    /**
     * 既存JPAエンティティへドメインモデルの内容を反映します。
     *
     * @param entity 更新対象エンティティ
     * @param inventory 在庫ドメインモデル
     */
    public static void updateEntity(
            InventoryJpaEntity entity,
            Inventory inventory
    ) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(inventory);
        entity.update(inventory.capacity(), inventory.available());
    }
}
