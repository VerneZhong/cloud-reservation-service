package com.verne.reservation.infrastructure.persistence.repository;

import com.verne.reservation.infrastructure.persistence.entity.ReservableEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 予約対象イベントのSpring Data JPAリポジトリです。
 */
public interface SpringDataReservableEventRepository
        extends JpaRepository<ReservableEventJpaEntity, UUID> {
}
