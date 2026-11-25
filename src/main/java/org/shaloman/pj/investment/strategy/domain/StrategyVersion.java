package org.shaloman.pj.investment.strategy.domain;
import java.time.LocalDateTime;
/** 전략 설정의 버전 단위 DB 모델입니다. */
public class StrategyVersion {
 private Long strategyId; private String strategyCode; private Integer versionNo; private String strategyName; private Boolean enabled; private String executionMode; private String configJson; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 public Long getStrategyId(){return strategyId;} public void setStrategyId(Long v){strategyId=v;}
 public String getStrategyCode(){return strategyCode;} public void setStrategyCode(String v){strategyCode=v;}
 public Integer getVersionNo(){return versionNo;} public void setVersionNo(Integer v){versionNo=v;}
 public String getStrategyName(){return strategyName;} public void setStrategyName(String v){strategyName=v;}
 public Boolean getEnabled(){return enabled;} public void setEnabled(Boolean v){enabled=v;}
 public String getExecutionMode(){return executionMode;} public void setExecutionMode(String v){executionMode=v;}
 public String getConfigJson(){return configJson;} public void setConfigJson(String v){configJson=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
 public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}