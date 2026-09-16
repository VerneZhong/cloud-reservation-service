package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservableEvent;
import com.verne.reservation.domain.repository.ReservableEventRepository;
import com.verne.reservation.infrastructure.persistence.entity.ReservableEventJpaEntity;
import com.verne.reservation.infrastructure.persistence.mapper.ReservableEventMapper;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * 予約対象イベントのドメインリポジトリをJPAで実装します。
 */
@Repository
public class JpaReservableEventRepositoryAdapter
        implements ReservableEventRepository {

    /** Spring Data JPAリポジトリです。 */
    private final SpringDataReservableEventRepository repository;

    /**
     * 永続化に使用するリポジトリを設定します。
     *
     * @param repository Spring Data JPAリポジトリ
     */
    public JpaReservableEventRepositoryAdapter(
            SpringDataReservableEventRepository repository
    ) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * イベントIDで予約対象イベントを検索します。
     *
     * @param id イベントID
     * @return 予約対象イベント
     */
    @Override
    public Optional<ReservableEvent> findById(EventId id) {
        Objects.requireNonNull(id);
        return repository.findById(id.value())
                .map(ReservableEventMapper::toDomain);
    }

    /**
     * 予約対象イベントを保存します。
     *
     * @param event 保存対象イベント
     * @return 保存されたイベント
     */
    @Override
    public ReservableEvent save(ReservableEvent event) {
        Objects.requireNonNull(event);
        ReservableEventJpaEntity entity = repository.findById(event.id().value())
                .map(existing -> {
                    ReservableEventMapper.updateEntity(existing, event);
                    return existing;
                })
                .orElseGet(() -> ReservableEventMapper.toNewEntity(event));
        return ReservableEventMapper.toDomain(repository.save(entity));
    }
}
