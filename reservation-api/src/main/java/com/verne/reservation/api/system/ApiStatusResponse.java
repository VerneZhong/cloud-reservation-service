package com.verne.reservation.api.system;

import java.time.Instant;
import java.util.Objects;

/**
 * APIの稼働状態を表すレスポンスです。
 *
 * @param service サービス名
 * @param status 稼働状態
 * @param timestamp 応答日時
 */
public record ApiStatusResponse(String service, String status, Instant timestamp) {

    /**
     * 稼働状態レスポンスの各項目がnullでないことを検証します。
     */
    public ApiStatusResponse {
        Objects.requireNonNull(service, "service must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
    }

    /**
     * 正常稼働中のレスポンスを生成します。
     *
     * @return 正常稼働中のレスポンス
     */
    public static ApiStatusResponse available() {
        return new ApiStatusResponse("reservation-api", "UP", Instant.now());
    }
}
