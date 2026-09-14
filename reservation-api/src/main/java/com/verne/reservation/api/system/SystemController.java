package com.verne.reservation.api.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIの基本情報および稼働状態を公開するコントローラーです。
 */
@RestController
@RequestMapping("/api/v1/status")
public class SystemController {

    /**
     * APIの稼働状態を返します。
     *
     * @return 稼働状態レスポンス
     */
    @GetMapping
    public ResponseEntity<ApiStatusResponse> getStatus() {
        return ResponseEntity.ok(ApiStatusResponse.available());
    }
}
