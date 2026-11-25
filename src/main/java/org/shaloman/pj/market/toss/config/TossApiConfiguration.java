package org.shaloman.pj.market.toss.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/** 서버 측 토스 Open API 호출 클라이언트를 구성합니다. */
@Configuration
@EnableConfigurationProperties(TossApiProperties.class)
public class TossApiConfiguration {
    /** 이 클라이언트는 Vue 브라우저가 아니라 백엔드 서비스만 사용합니다. */
    @Bean
    public RestClient tossRestClient(TossApiProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        // Spring Boot에서 Builder 빈을 자동 등록하는지에 의존하지 않도록 Framework API로 직접 생성합니다.
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
