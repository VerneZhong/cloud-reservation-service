package com.verne.reservation.infrastructure.persistence;

import com.verne.reservation.InfrastructureTestApplication;
import com.verne.reservation.application.port.out.DomainEventPublisher;
import com.verne.reservation.domain.event.ReservationCreatedEvent;
import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.EventStatus;
import com.verne.reservation.domain.model.Inventory;
import com.verne.reservation.domain.model.ReservableEvent;
import com.verne.reservation.domain.model.Reservation;
import com.verne.reservation.domain.model.ReservationId;
import com.verne.reservation.domain.model.ReservationStatus;
import com.verne.reservation.domain.model.UserId;
import com.verne.reservation.domain.repository.InventoryRepository;
import com.verne.reservation.domain.repository.ReservableEventRepository;
import com.verne.reservation.domain.repository.ReservationRepository;
import com.verne.reservation.infrastructure.outbox.SpringDataOutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PostgreSQLを使用してRepository AdapterとOutboxを検証します。
 */
@Testcontainers
@Transactional
@SpringBootTest(classes = InfrastructureTestApplication.class)
class RepositoryIntegrationTest {

    /** テストで使用する固定日時です。 */
    private static final Instant NOW =
            Instant.parse("2026-09-16T03:00:00Z");

    /** 結合テストで使用するPostgreSQLコンテナです。 */
    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine");

    /** イベントのドメインリポジトリです。 */
    @Autowired
    private ReservableEventRepository eventRepository;

    /** 在庫のドメインリポジトリです。 */
    @Autowired
    private InventoryRepository inventoryRepository;

    /** 予約のドメインリポジトリです。 */
    @Autowired
    private ReservationRepository reservationRepository;

    /** Outboxイベント発行ポートです。 */
    @Autowired
    private DomainEventPublisher eventPublisher;

    /** Outboxテーブルの確認に使用するリポジトリです。 */
    @Autowired
    private SpringDataOutboxEventRepository outboxRepository;

    /**
     * PostgreSQLコンテナの接続情報をSpringへ設定します。
     *
     * @param registry 動的プロパティ登録先
     */
    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> true);
    }

    /**
     * イベントと在庫を保存し、ドメインモデルとして復元できることを確認します。
     */
    @Test
    void persistsAndRestoresEventAndInventory() {
        EventId eventId = EventId.newId();
        ReservableEvent event = publishedEvent(eventId);
        eventRepository.save(event);
        inventoryRepository.save(new Inventory(eventId, 20));

        Inventory inventory = inventoryRepository.findByEventId(eventId)
                .orElseThrow();
        inventory.reserve(3);
        inventoryRepository.save(inventory);

        assertThat(eventRepository.findById(eventId))
                .get()
                .extracting(ReservableEvent::status)
                .isEqualTo(EventStatus.PUBLISHED);
        assertThat(inventoryRepository.findByEventId(eventId))
                .get()
                .extracting(Inventory::available)
                .isEqualTo(17);
    }

    /**
     * 予約を保存し、利用者単位で取得できることを確認します。
     */
    @Test
    void persistsAndFindsReservationByUser() {
        EventId eventId = EventId.newId();
        UserId userId = UserId.newId();
        eventRepository.save(publishedEvent(eventId));
        Reservation reservation = Reservation.createPending(
                ReservationId.newId(),
                userId,
                eventId,
                2,
                NOW,
                NOW.plus(15, ChronoUnit.MINUTES)
        );

        reservationRepository.save(reservation);

        assertThat(reservationRepository.findByUserId(userId))
                .singleElement()
                .satisfies(saved -> {
                    assertThat(saved.id()).isEqualTo(reservation.id());
                    assertThat(saved.status())
                            .isEqualTo(ReservationStatus.PENDING);
                });
    }

    /**
     * ドメインイベントが未送信Outboxレコードとして保存されることを確認します。
     */
    @Test
    void storesDomainEventInOutbox() {
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                ReservationId.newId(),
                UserId.newId(),
                EventId.newId(),
                2,
                NOW
        );

        eventPublisher.publish(event);
        outboxRepository.flush();

        assertThat(outboxRepository
                .findByPublishedAtIsNullOrderByCreatedAtAsc())
                .singleElement()
                .satisfies(saved -> {
                    assertThat(saved.getEventType())
                            .isEqualTo("ReservationCreatedEvent");
                    assertThat(saved.getAggregateId())
                            .isEqualTo(event.reservationId().value());
                    assertThat(saved.getQuantity()).isEqualTo(2);
                    assertThat(saved.getPublishedAt()).isNull();
                });
    }

    /**
     * テスト用の公開済みイベントを生成します。
     *
     * @param eventId イベントID
     * @return 公開済みイベント
     */
    private ReservableEvent publishedEvent(EventId eventId) {
        return new ReservableEvent(
                eventId,
                "AWS Workshop",
                "Cloud-native reservation workshop",
                NOW.plus(10, ChronoUnit.DAYS),
                NOW.minus(1, ChronoUnit.DAYS),
                NOW.plus(7, ChronoUnit.DAYS),
                EventStatus.PUBLISHED
        );
    }
}
