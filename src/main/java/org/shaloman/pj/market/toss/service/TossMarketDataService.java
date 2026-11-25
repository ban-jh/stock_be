package org.shaloman.pj.market.toss.service;

import tools.jackson.databind.JsonNode;
import org.shaloman.pj.market.toss.TossPriceQuote;
import org.shaloman.pj.market.toss.client.TossApiClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** 국내·미국 주식과 ETF 현재가를 조회하는 읽기 전용 어댑터입니다. */
@Service
public class TossMarketDataService {
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[A-Za-z0-9.-]+");
    private static final int MAX_SYMBOLS_PER_REQUEST = 200;
    private final TossApiClient tossApiClient;

    public TossMarketDataService(TossApiClient tossApiClient) {
        this.tossApiClient = tossApiClient;
    }

    /** 국내(예: 005930)와 미국(예: AAPL) 심볼을 함께 받아 현재가 API를 호출합니다. */
    public List<TossPriceQuote> getPrices(String commaSeparatedSymbols) {
        String symbols = validateSymbols(commaSeparatedSymbols);
        JsonNode response = tossApiClient.get("/api/v1/prices", Map.of("symbols", symbols), null);
        JsonNode result = response == null ? null : response.get("result");
        if (result == null || !result.isArray()) {
            throw new IllegalStateException("토스 현재가 응답의 result가 목록 형식이 아닙니다.");
        }
        List<TossPriceQuote> quotes = new ArrayList<>();
        for (JsonNode row : result) {
            String symbol = requiredText(row, "symbol");
            String timestamp = requiredText(row, "timestamp");
            String price = requiredText(row, "lastPrice");
            String currency = requiredText(row, "currency");
            try {
                quotes.add(new TossPriceQuote(symbol, timestamp, new BigDecimal(price), currency));
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("토스 현재가 응답의 가격 형식이 올바르지 않습니다.");
            }
        }
        return List.copyOf(quotes);
    }

    /** 토스 제공 JSON에서 타입이 확인된 필수 문자열 필드만 읽고 잘못된 응답은 명시적으로 거부합니다. */
    private String requiredText(JsonNode row, String field) {
        JsonNode value = row == null ? null : row.get(field);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            throw new IllegalStateException("토스 현재가 응답에 필수 필드가 없습니다: " + field);
        }
        return value.asText();
    }

    private String validateSymbols(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("조회할 종목 심볼을 입력해야 합니다.");
        }
        String[] symbols = Arrays.stream(input.split(",")).map(String::trim).toArray(String[]::new);
        if (symbols.length > MAX_SYMBOLS_PER_REQUEST) {
            throw new IllegalArgumentException("한 번의 현재가 조회에는 최대 200개 심볼만 요청할 수 있습니다.");
        }
        for (String symbol : symbols) {
            if (symbol.isEmpty() || !SYMBOL_PATTERN.matcher(symbol).matches()) {
                throw new IllegalArgumentException("종목 심볼은 영문, 숫자, 점, 하이픈만 사용할 수 있습니다.");
            }
        }
        return String.join(",", symbols);
    }
}
