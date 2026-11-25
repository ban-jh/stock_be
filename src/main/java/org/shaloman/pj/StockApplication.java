package org.shaloman.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AIM 백엔드 시작점입니다.
 * HolyLife와 같이 도메인별 모듈을 단일 Spring Boot 애플리케이션에 둡니다.
 * 자동 예약 실행 기능을 후속 추가할 수 있도록 스케줄러를 활성화합니다.
 */
@SpringBootApplication
@EnableScheduling
public class StockApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }
}