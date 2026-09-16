package com.verne.reservation.infrastructure.persistence.mapper;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.ReservableEvent;
import com.verne.reservation.infrastructure.persistence.entity.ReservableEventJpaEntity;

import java.util.Objects;

/**
 * 予約対象イベントのドメインモデルとJPAエンティティを相互変換します。
 */
public final class ReservableEventMapper {

    /**
     * インスタンス化を禁止します。
     */
    private ReservableEventMapper() {
    }

    /**
     * JPAエンティティをドメインモデルへ変換します。
     *
     * @param entity JPAエンティティ
     * @return 予約対象イベント
     */
    public static ReservableEvent toDomain(ReservableEventJpaEntity entity) {
        Objects.requireNonNull(entity);
        return new ReservableEvent(
                new EventId(entity.getId()),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartsAt(),
                entity.getBookingOpensAt(),
                entity.getBookingClosesAt(),
                entity.getStatus()
        );
    }

    /**
     * ドメインモデルから新規JPAエンティティを生成します。
     *
     * @param event 予約対象イベント
     * @return JPAエンティティ
     */
    public static ReservableEventJpaEntity toNewEntity(ReservableEvent event) {
        Objects.requireNonNull(event);
        return new ReservableEventJpaEntity(
                event.id().value(),
                event.title(),
                event.description(),
                event.startsAt(),
                event.bookingOpensAt(),
                event.bookingClosesAt(),
                event.status()
        );
    }

    /**
     * 既存JPAエンティティへドメインモデルの内容を反映します。
     *
     * @param entity 更新対象エンティティ
     * @param event 予約対象イベント
     */
    public static void updateEntity(
            ReservableEventJpaEntity entity,
            ReservableEvent event
    ) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(event);
        entity.update(
                event.title(),
                event.description(),
                event.startsAt(),
                event.bookingOpensAt(),
                event.bookingClosesAt(),
                event.status()
        );
    }
}
