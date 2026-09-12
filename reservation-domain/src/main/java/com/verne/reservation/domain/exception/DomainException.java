package com.verne.reservation.domain.exception;

/**
 * 予約ドメインのビジネスルール違反を表す基底例外です。
 */
public abstract class DomainException extends RuntimeException {

    /**
     * 指定されたメッセージでドメイン例外を生成します。
     *
     * @param message エラーメッセージ
     */
    protected DomainException(String message) {
        super(message);
    }
}
