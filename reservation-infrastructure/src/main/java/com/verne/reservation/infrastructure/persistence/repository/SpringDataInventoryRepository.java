package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.infrastructure.persistence.entity.InventoryJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * イベント在庫のSpring Data JPAリポジトリです。
 */
public interface SpringDataInventoryRepository
        extends JpaRepository<InventoryJpaEntity, UUID> {

    /**
     * 同時予約による在庫超過を防ぐため、行ロックを取得して在庫を検索します。
     *
     * @param eventId イベントID
     * @return 在庫エンティティ
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inventory from InventoryJpaEntity inventory "
            + "where inventory.eventId = :eventId")
    Optional<InventoryJpaEntity> findByEventIdForUpdate(
            @Param("eventId") UUID eventId
    );
}
