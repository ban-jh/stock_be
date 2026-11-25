package org.shaloman.pj.market.toss.client;

import tools.jackson.databind.JsonNode;
import org.shaloman.pj.market.toss.config.TossApiProperties;
import org.shaloman.pj.market.toss.config.TossRequestPacer;
import org.shaloman.pj.market.toss.exception.TossApiException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.time.Instant;
import java.util.Map;

/** 토스 OAuth 인증과 서버 간 REST 호출을 담당합니다. 토큰/비밀값을 로그에 출력하지 않습니다. */
@Component
public class TossApiClient {
    private static final String TOKEN_PATH = "/oauth2/token";
    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 60;
    private final RestClient restClient;
    private final TossApiProperties properties;
    private final TossRequestPacer requestPacer;
    private volatile CachedToken cachedToken;

    public TossApiClient(RestClient tossRestClient, TossApiProperties properties, TossRequestPacer requestPacer) {
        this.restClient = tossRestClient;
        this.properties = properties;
        this.requestPacer = requestPacer;
    }

    /** API GET을 실행하고 계좌 범위 API에는 accountSeq 헤더를 추가합니다. */
    public JsonNode get(String path, Map<String, String> queryParameters, String accountSeq) {
        properties.validateForRequest();
        try {
            requestPacer.waitForTurn();
            return restClient.get()
                    .uri(uriBuilder -> buildUri(uriBuilder, path, queryParameters))
                    .headers(headers -> {
                        headers.setBearerAuth(accessToken());
                        if (accountSeq != null && !accountSeq.isBlank()) {
                            headers.set("X-Tossinvest-Account", accountSeq);
                        }
                    })
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw mapHttpError(exception);
        } catch (RestClientException exception) {
            throw new TossApiException("TOSS_NETWORK_ERROR", "토스증권 API와 통신하지 못했습니다.", exception);
        }
    }

    private java.net.URI buildUri(UriBuilder builder, String path, Map<String, String> queryParameters) {
        UriBuilder result = builder.path(path);
        queryParameters.forEach(result::queryParam);
        return result.build();
    }

    /** 클라이언트당 토큰 하나를 발급하므로 이 프로세스 안에서 발급을 직렬화합니다. */
    private synchronized String accessToken() {
        Instant now = Instant.now();
        if (cachedToken != null && now.isBefore(cachedToken.expiresAt())) {
            return cachedToken.value();
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        try {
            requestPacer.waitForTurn();
            JsonNode response = restClient.post()
                    .uri(TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(JsonNode.class);
            if (response == null || !response.path("access_token").isTextual()) {
                throw new TossApiException("TOSS_INVALID_TOKEN_RESPONSE", "토스증권 인증 응답에 액세스 토큰이 없습니다.");
            }
            long expiresIn = response.path("expires_in").asLong(0);
            if (expiresIn <= TOKEN_EXPIRY_BUFFER_SECONDS) {
                throw new TossApiException("TOSS_INVALID_TOKEN_EXPIRY", "토스증권 인증 응답의 만료 시간이 올바르지 않습니다.");
            }
            String token = response.path("access_token").asText();
            cachedToken = new CachedToken(token, now.plusSeconds(expiresIn - TOKEN_EXPIRY_BUFFER_SECONDS));
            return token;
        } catch (RestClientResponseException exception) {
            throw mapHttpError(exception);
        } catch (RestClientException exception) {
            throw new TossApiException("TOSS_AUTH_NETWORK_ERROR", "토스증권 인증 서버와 통신하지 못했습니다.", exception);
        }
    }

    /** 외부 응답 본문은 자격증명이 섞일 수 있어 오류 메시지에 포함하지 않습니다. */
    private TossApiException mapHttpError(RestClientResponseException exception) {
        int status = exception.getStatusCode().value();
        String code = switch (status) {
            case 400 -> "TOSS_BAD_REQUEST";
            case 401 -> "TOSS_UNAUTHORIZED";
            case 403 -> "TOSS_IP_NOT_ALLOWED";
            case 404 -> "TOSS_RESOURCE_NOT_FOUND";
            case 429 -> "TOSS_RATE_LIMITED";
            default -> status >= 500 ? "TOSS_SERVER_ERROR" : "TOSS_HTTP_ERROR";
        };
        String message = switch (status) {
            case 401 -> "토스증권 인증에 실패했습니다. 앱키·시크릿키와 토큰 상태를 확인하세요.";
            case 403 -> "요청 서버 IP가 토스 Open API 허용 IP에 등록되어 있는지 확인하세요.";
            case 404 -> "요청한 API 경로나 종목을 찾을 수 없습니다.";
            case 429 -> "토스증권 API 호출 한도를 초과했습니다. 호출량을 줄이고 재시도 간격을 두세요.";
            default -> "토스증권 API 요청이 실패했습니다. HTTP 상태 코드 " + status + "를 확인하세요.";
        };
        String retryAfter = status == 429 && exception.getResponseHeaders() != null
                ? exception.getResponseHeaders().getFirst("Retry-After") : null;
        return new TossApiException(status, code, message, retryAfter);
    }

    private record CachedToken(String value, Instant expiresAt) {}
}
