package org.shaloman.pj.market.toss.service;

import tools.jackson.databind.JsonNode;
import org.shaloman.pj.market.toss.TossAccountSummary;
import org.shaloman.pj.market.toss.client.TossApiClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/** 본인 계좌 목록과 보유 자산을 읽기 전용으로 조회하는 어댑터입니다. 주문 API는 호출하지 않습니다. */
@Service
public class TossAccountService {
    private final TossApiClient tossApiClient;

    public TossAccountService(TossApiClient tossApiClient) {
        this.tossApiClient = tossApiClient;
    }

    /** 계좌 목록을 조회합니다. 응답에는 계좌번호 등 민감정보가 포함될 수 있습니다. */
    public List<TossAccountSummary> getAccounts() {
        JsonNode response = tossApiClient.get("/api/v1/accounts", Map.of(), null);
        JsonNode result = response == null ? null : response.get("result");
        if (result == null || !result.isArray()) {
            throw new IllegalStateException("토스 계좌 응답의 result가 목록 형식이 아닙니다.");
        }
        List<TossAccountSummary> accounts = new ArrayList<>();
        for (JsonNode row : result) {
            String accountSeq = requiredText(row, "accountSeq");
            if (!accountSeq.matches("\\d+")) {
                throw new IllegalStateException("토스 계좌 응답의 accountSeq 형식이 올바르지 않습니다.");
            }
            accounts.add(new TossAccountSummary(accountSeq, requiredText(row, "accountType"), maskAccountNo(requiredText(row, "accountNo"))));
        }
        return List.copyOf(accounts);
    }

    /** 사람이 보는 계좌번호가 아닌 accounts 응답의 숫자 accountSeq를 사용합니다. */
    public JsonNode getHoldings(String accountSeq) {
        if (accountSeq == null || !accountSeq.matches("\\d+")) {
            throw new IllegalArgumentException("보유 자산 조회에는 숫자 accountSeq가 필요합니다.");
        }
        return tossApiClient.get("/api/v1/holdings", Map.of(), accountSeq);
    }

    /** 공통 accountNo 원문을 내부 계좌 요약이나 프론트에 그대로 넘기지 않도록 뒷 4자리만 남깁니다. */
    private String maskAccountNo(String accountNo) {
        String digits = accountNo.replaceAll("\\D", "");
        int visibleDigits = Math.min(4, digits.length());
        return "*".repeat(Math.max(4, digits.length() - visibleDigits)) + digits.substring(digits.length() - visibleDigits);
    }

    private String requiredText(JsonNode row, String field) {
        JsonNode value = row == null ? null : row.get(field);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            throw new IllegalStateException("토스 계좌 응답에 필수 필드가 없습니다: " + field);
        }
        return value.asText();
    }
}
