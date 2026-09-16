package com.verne.reservation.infrastructure.persistence.mapper;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.infrastructure.persistence.entity.ReservationJpaEntity;

import java.util.Objects;

/**
 * 予約のドメインモデルとJPAエンティティを相互変換します。
 */
public final class ReservationMapper {

    /**
     * インスタンス化を禁止します。
     */
    private ReservationMapper() {
    }

    /**
     * JPAエンティティをドメインモデルへ変換します。
     *
     * @param entity JPAエンティティ
     * @return 予約ドメインモデル
     */
    public static Reservation toDomain(ReservationJpaEntity entity) {
        Objects.requireNonNull(entity);
        return new Reservation(
                new ReservationId(entity.getId()),
                new UserId(entity.getUserId()),
                new EventId(entity.getEventId()),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getConfirmedAt(),
                entity.getCancelledAt()
        );
    }

    /**
     * ドメインモデルから新規JPAエンティティを生成します。
     *
     * @param reservation 予約ドメインモデル
     * @return JPAエンティティ
     */
    public static ReservationJpaEntity toNewEntity(Reservation reservation) {
        Objects.requireNonNull(reservation);
        return new ReservationJpaEntity(
                reservation.id().value(),
                reservation.userId().value(),
                reservation.eventId().value(),
                reservation.quantity(),
                reservation.status(),
                reservation.createdAt(),
                reservation.expiresAt(),
                reservation.confirmedAt(),
                reservation.cancelledAt()
        );
    }

    /**
     * 既存JPAエンティティへドメインモデルの内容を反映します。
     *
     * @param entity 更新対象エンティティ
     * @param reservation 予約ドメインモデル
     */
    public static void updateEntity(
            ReservationJpaEntity entity,
            Reservation reservation
    ) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(reservation);
        entity.update(
                reservation.quantity(),
                reservation.status(),
                reservation.createdAt(),
                reservation.expiresAt(),
                reservation.confirmedAt(),
                reservation.cancelledAt()
        );
    }
}
