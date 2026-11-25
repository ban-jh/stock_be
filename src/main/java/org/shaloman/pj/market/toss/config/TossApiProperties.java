package org.shaloman.pj.market.toss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 토스증권 연동 설정이며 인증 비밀값은 OS 환경변수에서만 받습니다. */
@ConfigurationProperties(prefix = "stock.toss")
public record TossApiProperties(boolean enabled, boolean smokeCheck, String baseUrl, String clientId,
                                String clientSecret, long minRequestIntervalMillis) {
    /** 애플리케이션 기동은 키 없이 허용하고, 실제 API 요청 직전에 필수 설정을 확인합니다. */
    public void validateForRequest() {
        if (!enabled) {
            throw new IllegalStateException("토스증권 API 연동이 비활성화되어 있습니다.");
        }
        if ((clientId != null && clientId.startsWith("DPAPI:"))
                || (clientSecret != null && clientSecret.startsWith("DPAPI:"))) {
            throw new IllegalStateException("DPAPI 암호문은 직접 사용할 수 없습니다. .\\run-local.ps1로 서버를 실행하세요.");
        }
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("TOSS_CLIENT_ID와 TOSS_CLIENT_SECRET 환경변수를 설정해야 합니다.");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("토스증권 API 기본 URL 설정이 비어 있습니다.");
        }
        if (minRequestIntervalMillis < 0) {
            throw new IllegalStateException("토스 API 요청 간격은 0 이상이어야 합니다.");
        }
    }
}
