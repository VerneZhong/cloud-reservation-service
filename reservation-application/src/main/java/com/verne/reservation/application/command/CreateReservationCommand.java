package com.verne.reservation.application.command;

import com.verne.reservation.domain.model.EventId;
import com.verne.reservation.domain.model.UserId;

import java.util.Objects;

/**
 * 予約作成ユースケースへの入力値です。
 *
 * @param userId 利用者ID
 * @param eventId イベントID
 * @param quantity 予約数
 */
public record CreateReservationCommand(UserId userId, EventId eventId, int quantity) {

    /**
     * 入力値を検証して予約作成コマンドを生成します。
     */
    public CreateReservationCommand {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}
