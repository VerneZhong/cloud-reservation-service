package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.infrastructure.persistence.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 予約のSpring Data JPAリポジトリです。
 */
public interface SpringDataReservationRepository
        extends JpaRepository<ReservationJpaEntity, UUID> {

    /**
     * 利用者IDに紐づく予約を作成日時の降順で取得します。
     *
     * @param userId 利用者ID
     * @return 予約エンティティ一覧
     */
    List<ReservationJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
