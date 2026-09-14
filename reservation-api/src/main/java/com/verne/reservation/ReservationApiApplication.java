package com.verne.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 予約APIを起動するSpring Bootアプリケーションです。
 */
@SpringBootApplication
public class ReservationApiApplication {

    /**
     * 予約APIを起動します。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(ReservationApiApplication.class, args);
    }
}
