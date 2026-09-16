package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.repository.InventoryRepository;
import com.verne.reservation.infrastructure.persistence.entity.InventoryJpaEntity;
import com.verne.reservation.infrastructure.persistence.mapper.InventoryMapper;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * イベント在庫のドメインリポジトリをJPAで実装します。
 */
@Repository
public class JpaInventoryRepositoryAdapter implements InventoryRepository {

    /** Spring Data JPAリポジトリです。 */
    private final SpringDataInventoryRepository repository;

    /**
     * 永続化に使用するリポジトリを設定します。
     *
     * @param repository Spring Data JPAリポジトリ
     */
    public JpaInventoryRepositoryAdapter(
            SpringDataInventoryRepository repository
    ) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * 排他ロックを取得してイベント在庫を検索します。
     *
     * @param eventId イベントID
     * @return イベント在庫
     */
    @Override
    public Optional<Inventory> findByEventId(EventId eventId) {
        Objects.requireNonNull(eventId);
        return repository.findByEventIdForUpdate(eventId.value())
                .map(InventoryMapper::toDomain);
    }

    /**
     * イベント在庫を保存します。
     *
     * @param inventory 保存対象在庫
     * @return 保存された在庫
     */
    @Override
    public Inventory save(Inventory inventory) {
        Objects.requireNonNull(inventory);
        InventoryJpaEntity entity = repository
                .findById(inventory.eventId().value())
                .map(existing -> {
                    InventoryMapper.updateEntity(existing, inventory);
                    return existing;
                })
                .orElseGet(() -> InventoryMapper.toNewEntity(inventory));
        return InventoryMapper.toDomain(repository.save(entity));
    }
}
