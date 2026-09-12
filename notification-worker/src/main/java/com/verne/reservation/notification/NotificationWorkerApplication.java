package com.verne.reservation.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Verne
 * @since 2026-09-12
 */
@SpringBootApplication(scanBasePackages = {"com.verne.reservation"})
public class NotificationWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationWorkerApplication.class, args);
    }
}
