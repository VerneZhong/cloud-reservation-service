package com.verne.reservation.api.common;

import java.util.Objects;

/**
 * リクエスト項目の入力エラーを表します。
 *
 * @param field エラーが発生した項目名
 * @param message エラーメッセージ
 */
public record FieldViolation(String field, String message) {

    /**
     * 入力エラーの各項目がnullでないことを検証します。
     */
    public FieldViolation {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }
}
