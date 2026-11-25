package org.shaloman.pj.market.toss.config;

import org.shaloman.pj.market.toss.service.TossAccountService;
import org.shaloman.pj.market.toss.service.TossMarketDataService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * TOSS_SMOKE_CHECK=true일 때만 실행되는 읽기 전용 연결 점검입니다.
 * 시세 값, 계좌번호, 토큰 등 민감한 응답 필드는 출력하지 않습니다.
 */
@Component
@ConditionalOnProperty(prefix = "stock.toss", name = "smoke-check", havingValue = "true")
public class TossApiSmokeCheckRunner implements ApplicationRunner {
    private final TossMarketDataService marketDataService;
    private final TossAccountService accountService;

    public TossApiSmokeCheckRunner(TossMarketDataService marketDataService, TossAccountService accountService) {
        this.marketDataService = marketDataService;
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) {
        var prices = marketDataService.getPrices("005930,AAPL");
        var accounts = accountService.getAccounts();
        int priceRows = prices.size();
        int accountRows = accounts.size();
        if (priceRows != 2) {
            throw new IllegalStateException("토스 현재가 조회 결과가 예상과 다릅니다. 기대 2건, 응답 " + priceRows + "건");
        }
        if (accountRows < 1) {
            throw new IllegalStateException("토스 계좌 목록이 비어 있습니다. 계좌 인증 및 Open API 권한을 확인하세요.");
        }
        System.out.printf("Toss Open API read-only check succeeded. priceRows=%d, accountRows=%d%n", priceRows, accountRows);
    }

}
