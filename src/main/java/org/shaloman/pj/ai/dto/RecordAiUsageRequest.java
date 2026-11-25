package org.shaloman.pj.ai.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
/** 공급자 호출이 끝난 뒤 AI 사용량을 누적하는 요청입니다. */
public record RecordAiUsageRequest(
 @NotNull LocalDate usageDate,
 @NotBlank @Size(max=40) String providerCode,
 @Min(1) long callCount,
 @Min(0) long inputTokens,
 @Min(0) long outputTokens,
 @NotNull @DecimalMin("0.0") BigDecimal estimatedCost
){}