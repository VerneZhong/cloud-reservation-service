package com.verne.reservation.domain.model;

import com.verne.reservation.domain.exception.ReservationWindowClosedException;

import java.time.Instant;
import java.util.Objects;

/**
 * イベント情報、公開状態および予約受付期間を管理する集約です。
 */
public final class ReservableEvent {

    /**
     * イベントIDです。
     */
    private final EventId id;
    /**
     * イベント名です。
     */
    private final String title;
    /**
     * イベントの説明です。
     */
    private final String description;
    /**
     * イベント開始日時です。
     */
    private final Instant startsAt;
    /**
     * 予約受付開始日時です。
     */
    private final Instant bookingOpensAt;
    /**
     * 予約受付終了日時です。
     */
    private final Instant bookingClosesAt;
    /**
     * 現在のイベント状態です。
     */
    private EventStatus status;

    /**
     * イベント情報を生成または永続化データから復元します。
     *
     * @param id イベントID
     * @param title イベント名
     * @param description イベントの説明
     * @param startsAt イベント開始日時
     * @param bookingOpensAt 予約受付開始日時
     * @param bookingClosesAt 予約受付終了日時
     * @param status イベント状態
     */
    public ReservableEvent(
            EventId id,
            String title,
            String description,
            Instant startsAt,
            Instant bookingOpensAt,
            Instant bookingClosesAt,
            EventStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = requireText(title, "title");
        this.description = Objects.requireNonNullElse(description, "");
        this.startsAt = Objects.requireNonNull(startsAt, "startsAt must not be null");
        this.bookingOpensAt = Objects.requireNonNull(bookingOpensAt, "bookingOpensAt must not be null");
        this.bookingClosesAt = Objects.requireNonNull(bookingClosesAt, "bookingClosesAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");

        if (!bookingOpensAt.isBefore(bookingClosesAt)) {
            throw new IllegalArgumentException("bookingOpensAt must be before bookingClosesAt");
        }
        if (bookingClosesAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("bookingClosesAt must not be after startsAt");
        }
    }

    /**
     * 下書き状態のイベントを公開します。
     */
    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException("Only a draft event can be published");
        }
        status = EventStatus.PUBLISHED;
    }

    /**
     * 公開中のイベントの予約受付を終了します。
     */
    public void close() {
        if (status != EventStatus.PUBLISHED) {
            throw new IllegalStateException("Only a published event can be closed");
        }
        status = EventStatus.CLOSED;
    }

    /**
     * イベントを中止状態へ変更します。
     */
    public void cancel() {
        if (status == EventStatus.CANCELLED) {
            return;
        }
        status = EventStatus.CANCELLED;
    }

    /**
     * 指定日時に予約可能であることを検証します。
     *
     * @param now 判定日時
     * @throws ReservationWindowClosedException 予約できない場合
     */
    public void ensureReservableAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (status != EventStatus.PUBLISHED
                || now.isBefore(bookingOpensAt)
                || !now.isBefore(bookingClosesAt)) {
            throw new ReservationWindowClosedException();
        }
    }

    /**
     * 指定日時に予約可能かを判定します。
     *
     * @param now 判定日時
     * @return 予約可能な場合はtrue
     */
    public boolean isReservableAt(Instant now) {
        try {
            ensureReservableAt(now);
            return true;
        } catch (ReservationWindowClosedException exception) {
            return false;
        }
    }

    /**
     * 文字列が空でないことを検証し、前後の空白を除去します。
     *
     * @param value 検証対象の値
     * @param fieldName フィールド名
     * @return 正規化した文字列
     */
    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    /**
     * イベントIDを返します。
     *
     * @return イベントID
     */
    public EventId id() {
        return id;
    }

    /**
     * イベント名を返します。
     *
     * @return イベント名
     */
    public String title() {
        return title;
    }

    /**
     * イベントの説明を返します。
     *
     * @return イベントの説明
     */
    public String description() {
        return description;
    }

    /**
     * イベント開始日時を返します。
     *
     * @return イベント開始日時
     */
    public Instant startsAt() {
        return startsAt;
    }

    /**
     * 予約受付開始日時を返します。
     *
     * @return 予約受付開始日時
     */
    public Instant bookingOpensAt() {
        return bookingOpensAt;
    }

    /**
     * 予約受付終了日時を返します。
     *
     * @return 予約受付終了日時
     */
    public Instant bookingClosesAt() {
        return bookingClosesAt;
    }

    /**
     * イベント状態を返します。
     *
     * @return イベント状態
     */
    public EventStatus status() {
        return status;
    }
}
