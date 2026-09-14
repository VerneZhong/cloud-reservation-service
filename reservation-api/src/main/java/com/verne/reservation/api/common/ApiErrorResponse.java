package com.verne.reservation.api.common;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * APIで共通利用するエラーレスポンスです。
 *
 * @param timestamp エラー発生日時
 * @param status HTTPステータスコード
 * @param code アプリケーション固有のエラーコード
 * @param message エラー内容
 * @param path リクエストパス
 * @param violations 項目単位の入力エラー
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldViolation> violations
) {

    /**
     * エラーレスポンスの各項目を検証し、入力エラー一覧を変更不可にします。
     */
    public ApiErrorResponse {
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(path, "path must not be null");
        violations = violations == null ? List.of() : List.copyOf(violations);
    }

    /**
     * 項目エラーを含まないエラーレスポンスを生成します。
     *
     * @param status HTTPステータスコード
     * @param code アプリケーション固有のエラーコード
     * @param message エラー内容
     * @param path リクエストパス
     * @return エラーレスポンス
     */
    public static ApiErrorResponse of(int status, String code, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, code, message, path, List.of());
    }

    /**
     * 項目エラーを含むエラーレスポンスを生成します。
     *
     * @param status HTTPステータスコード
     * @param code アプリケーション固有のエラーコード
     * @param message エラー内容
     * @param path リクエストパス
     * @param violations 項目単位の入力エラー
     * @return エラーレスポンス
     */
    public static ApiErrorResponse of(
            int status,
            String code,
            String message,
            String path,
            List<FieldViolation> violations
    ) {
        return new ApiErrorResponse(Instant.now(), status, code, message, path, violations);
    }
}
