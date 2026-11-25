package org.shaloman.pj.investment.strategy.dto;
import jakarta.validation.constraints.*;
import java.util.Map;
/** 전략 버전 생성 요청. 새 버전은 항상 비활성 PAPER 상태로 등록합니다. */
public record CreateStrategyVersionRequest(@NotBlank String strategyCode,@Min(1) int versionNo,@NotBlank String strategyName,@NotNull Map<String,Object> config){}