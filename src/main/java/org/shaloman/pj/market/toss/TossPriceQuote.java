package org.shaloman.pj.market.toss;

import java.math.BigDecimal;

/** 토스 현재가 응답의 안정적으로 확인된 필드를 내부 시세 모델로 옮깁니다. */
public record TossPriceQuote(String symbol, String timestamp, BigDecimal lastPrice, String currency) {}
