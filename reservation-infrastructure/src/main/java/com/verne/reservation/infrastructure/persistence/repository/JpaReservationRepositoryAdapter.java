package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.domain.repository.ReservationRepository;
import com.verne.reservation.infrastructure.persistence.entity.ReservationJpaEntity;
import com.verne.reservation.infrastructure.persistence.mapper.ReservationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 予約のドメインリポジトリをJPAで実装します。
 */
@Repository
public class JpaReservationRepositoryAdapter implements ReservationRepository {

    /** Spring Data JPAリポジトリです。 */
    private final SpringDataReservationRepository repository;

    /**
     * 永続化に使用するリポジトリを設定します。
     *
     * @param repository Spring Data JPAリポジトリ
     */
    public JpaReservationRepositoryAdapter(
            SpringDataReservationRepository repository
    ) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * 予約IDで予約を検索します。
     *
     * @param id 予約ID
     * @return 予約
     */
    @Override
    public Optional<Reservation> findById(ReservationId id) {
        Objects.requireNonNull(id);
        return repository.findById(id.value())
                .map(ReservationMapper::toDomain);
    }

    /**
     * 利用者IDに紐づく予約一覧を取得します。
     *
     * @param userId 利用者ID
     * @return 予約一覧
     */
    @Override
    public List<Reservation> findByUserId(UserId userId) {
        Objects.requireNonNull(userId);
        return repository.findByUserIdOrderByCreatedAtDesc(userId.value())
                .stream()
                .map(ReservationMapper::toDomain)
                .toList();
    }

    /**
     * 予約を保存します。
     *
     * @param reservation 保存対象予約
     * @return 保存された予約
     */
    @Override
    public Reservation save(Reservation reservation) {
        Objects.requireNonNull(reservation);
        ReservationJpaEntity entity = repository
                .findById(reservation.id().value())
                .map(existing -> {
                    ReservationMapper.updateEntity(existing, reservation);
                    return existing;
                })
                .orElseGet(() -> ReservationMapper.toNewEntity(reservation));
        return ReservationMapper.toDomain(repository.save(entity));
    }
}
