package com.verne.reservation.domain.exception;

/**
 * 要求数が予約可能な在庫数を超えた場合に発生する例外です。
 */
public final class InsufficientInventoryException extends DomainException {

    /**
     * 要求数と予約可能数を使用して例外を生成します。
     *
     * @param requested 要求された予約数
     * @param available 現在の予約可能数
     */
    public InsufficientInventoryException(int requested, int available) {
        super("Insufficient inventory: requested=%d, available=%d".formatted(requested, available));
    }
}
