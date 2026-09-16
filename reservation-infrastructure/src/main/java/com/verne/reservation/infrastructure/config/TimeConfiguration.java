package com.verne.reservation.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * アプリケーション全体で使用する時刻関連Beanを定義します。
 */
@Configuration
public class TimeConfiguration {

    /**
     * サーバーのタイムゾーンに依存しないUTCクロックを提供します。
     *
     * @return UTCクロック
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
