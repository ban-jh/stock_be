package org.shaloman.pj.system.controller;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.shaloman.pj.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프론트 화면이 서버 프로세스 응답 여부를 확인하는 API입니다.
 * UP은 프로세스 상태만 의미하며 DB·토스·AI 연동 성공과는 구분합니다.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
            "application", "stock-be",
            "status", "UP",
            "executionMode", "PAPER",
            "brokerConnected", false,
            "aiEnabled", false,
            "checkedAt", OffsetDateTime.now(ZoneId.of("Asia/Seoul")).toString()
        ));
    }
}