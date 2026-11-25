package org.shaloman.pj.ai.domain;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
/** 일자·제공자 단위 AI 사용량 집계 행입니다. */
public class AiUsageDaily {
 private LocalDate usageDate; private String providerCode; private Long callCount; private Long inputTokens; private Long outputTokens; private BigDecimal estimatedCost; private LocalDateTime updatedAt;
 public LocalDate getUsageDate(){return usageDate;} public void setUsageDate(LocalDate v){usageDate=v;}
 public String getProviderCode(){return providerCode;} public void setProviderCode(String v){providerCode=v;}
 public Long getCallCount(){return callCount;} public void setCallCount(Long v){callCount=v;}
 public Long getInputTokens(){return inputTokens;} public void setInputTokens(Long v){inputTokens=v;}
 public Long getOutputTokens(){return outputTokens;} public void setOutputTokens(Long v){outputTokens=v;}
 public BigDecimal getEstimatedCost(){return estimatedCost;} public void setEstimatedCost(BigDecimal v){estimatedCost=v;}
 public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}