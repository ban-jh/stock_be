package org.shaloman.pj.market.toss;

import tools.jackson.databind.JsonNode;
import org.shaloman.pj.common.api.ApiResponse;
import org.shaloman.pj.market.toss.TossAccountSummary;
import org.shaloman.pj.market.toss.TossPriceQuote;
import org.shaloman.pj.market.toss.service.TossAccountService;
import org.shaloman.pj.market.toss.service.TossMarketDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** 인증된 개인 프론트엔드에 읽기 전용 시세·계좌 API를 제공합니다. 주문 경로는 노출하지 않습니다. */
@RestController
@RequestMapping("/api/toss")
public class TossMarketController {
    private final TossMarketDataService marketDataService;
    private final TossAccountService accountService;

    public TossMarketController(TossMarketDataService marketDataService, TossAccountService accountService) {
        this.marketDataService = marketDataService;
        this.accountService = accountService;
    }

    /** 국내·미국 주식 및 ETF 현재가를 조회합니다. 응답 본문은 토스 제공 필드를 유지합니다. */
    @GetMapping("/prices")
    public ApiResponse<List<TossPriceQuote>> prices(@RequestParam String symbols) {
        return ApiResponse.success(marketDataService.getPrices(symbols));
    }

    /** 계좌 식별정보가 포함될 수 있는 본인 계좌 목록을 반환합니다. */
    @GetMapping("/accounts")
    public ApiResponse<List<TossAccountSummary>> accounts() {
        return ApiResponse.success(accountService.getAccounts());
    }

    /** accounts 응답에서 얻은 숫자 accountSeq로 보유 자산을 조회합니다. */
    @GetMapping("/accounts/{accountSeq}/holdings")
    public ApiResponse<JsonNode> holdings(@PathVariable String accountSeq) {
        return ApiResponse.success(accountService.getHoldings(accountSeq));
    }
}
