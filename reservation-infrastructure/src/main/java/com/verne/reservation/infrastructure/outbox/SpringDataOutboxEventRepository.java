package com.verne.reservation.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * OutboxイベントのSpring Data JPAリポジトリです。
 */
public interface SpringDataOutboxEventRepository
        extends JpaRepository<OutboxEventJpaEntity, UUID> {

    /**
     * 未送信イベントを登録日時の昇順で取得します。
     *
     * @return 未送信イベント一覧
     */
    List<OutboxEventJpaEntity> findByPublishedAtIsNullOrderByCreatedAtAsc();
}
