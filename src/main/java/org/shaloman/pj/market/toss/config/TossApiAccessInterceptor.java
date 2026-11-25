package org.shaloman.pj.market.toss.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** 토스 조회 경로를 개인용 백엔드 접근 토큰으로 보호합니다. 토큰은 URL이나 로그에 기록하지 않습니다. */
@Component
public class TossApiAccessInterceptor implements HandlerInterceptor {
    private final String configuredToken;

    public TossApiAccessInterceptor(@Value("${stock.api.access-token:}") String configuredToken) {
        this.configuredToken = configuredToken == null ? "" : configuredToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 브라우저 CORS 사전 요청에는 Bearer 값이 없으므로 사전 요청은 CORS 설정에서만 제한합니다.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        if (configuredToken.isBlank()) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API_ACCESS_NOT_CONFIGURED", "토스 조회 API의 개인 접근 토큰을 설정해야 합니다.");
            return false;
        }

        String authorization = request.getHeader("Authorization");
        String suppliedToken = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7) : "";
        boolean authorized = MessageDigest.isEqual(
                configuredToken.getBytes(StandardCharsets.UTF_8), suppliedToken.getBytes(StandardCharsets.UTF_8));
        if (!authorized) {
            response.setHeader("WWW-Authenticate", "Bearer");
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "API_UNAUTHORIZED", "토스 조회 API 접근 인증에 실패했습니다.");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"data\":null,\"error\":{\"code\":\""
                + code + "\",\"message\":\"" + message + "\"}}");
    }
}
