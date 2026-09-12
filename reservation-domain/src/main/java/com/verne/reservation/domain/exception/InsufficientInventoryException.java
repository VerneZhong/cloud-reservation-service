package com.verne.reservation.domain.exception;

/**
 * 要求数が予約可能な在庫数を超えた場合に発生する例外です。
 */
public final class InsufficientInventoryException extends DomainException {

    public InsufficientInventoryException(int requested, int available) {
        super("Insufficient inventory: requested=%d, available=%d".formatted(requested, available));
    }
}
