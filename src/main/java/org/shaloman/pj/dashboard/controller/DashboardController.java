package org.shaloman.pj.dashboard.controller;

import java.util.Map;

import org.shaloman.pj.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드 API 진입점입니다.
 * 토스 계좌 연동 전에는 임의 잔고나 수익률을 만들지 않고 미연결 상태를 전달합니다.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @GetMapping("/summary")
    public ApiResponse<Map<String, String>> summary() {
        return ApiResponse.success(Map.of(
            "executionMode", "PAPER",
            "brokerStatus", "NOT_CONNECTED",
            "marketDataStatus", "NOT_CONNECTED",
            "aiStatus", "DISABLED",
            "portfolioStatus", "NOT_AVAILABLE"
        ));
    }
}