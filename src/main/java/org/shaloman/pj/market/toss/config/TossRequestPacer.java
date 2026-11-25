package org.shaloman.pj.market.toss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 토큰 발급과 데이터 조회를 포함해 이 프로세스의 토스 호출 간격을 보수적으로 유지합니다. */
@Component
public class TossRequestPacer {
    private final long minimumIntervalMillis;
    private long nextRequestAtMillis;

    public TossRequestPacer(@Value("${stock.toss.min-request-interval-millis:500}") long minimumIntervalMillis) {
        if (minimumIntervalMillis < 0) {
            throw new IllegalArgumentException("토스 API 요청 간격은 0 이상이어야 합니다.");
        }
        this.minimumIntervalMillis = minimumIntervalMillis;
    }

    /** 동시 요청도 직렬화하여 설정한 최소 간격을 지킵니다. 0은 제한을 끄는 명시 설정입니다. */
    public synchronized void waitForTurn() {
        if (minimumIntervalMillis == 0) return;
        long now = System.currentTimeMillis();
        long waitMillis = Math.max(0, nextRequestAtMillis - now);
        nextRequestAtMillis = Math.max(now, nextRequestAtMillis) + minimumIntervalMillis;
        if (waitMillis == 0) return;
        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("토스 API 호출 대기 중 작업이 중단되었습니다.", exception);
        }
    }
}
