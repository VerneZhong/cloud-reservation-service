package com.verne.reservation;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Infrastructure結合テストで使用するSpring Boot設定です。
 */
@SpringBootApplication
public class InfrastructureTestApplication {

    /**
     * テスト専用設定のため、インスタンス化のみを許可します。
     */
    public InfrastructureTestApplication() {
    }
}
