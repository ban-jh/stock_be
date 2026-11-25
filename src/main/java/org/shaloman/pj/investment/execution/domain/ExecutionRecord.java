package org.shaloman.pj.investment.execution.domain;
import java.time.LocalDateTime;
/** 과거 실행 한 건의 응답/DB 모델입니다. */
public class ExecutionRecord {
 private String executionId,idempotencyKey,triggerType,executionMode,status,strategyCode,stopReason,summaryJson;
 private Integer strategyVersion; private LocalDateTime startedAt,finishedAt;
 public String getExecutionId(){return executionId;} public void setExecutionId(String v){executionId=v;}
 public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;}
 public String getTriggerType(){return triggerType;} public void setTriggerType(String v){triggerType=v;}
 public String getExecutionMode(){return executionMode;} public void setExecutionMode(String v){executionMode=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getStrategyCode(){return strategyCode;} public void setStrategyCode(String v){strategyCode=v;}
 public Integer getStrategyVersion(){return strategyVersion;} public void setStrategyVersion(Integer v){strategyVersion=v;}
 public LocalDateTime getStartedAt(){return startedAt;} public void setStartedAt(LocalDateTime v){startedAt=v;}
 public LocalDateTime getFinishedAt(){return finishedAt;} public void setFinishedAt(LocalDateTime v){finishedAt=v;}
 public String getStopReason(){return stopReason;} public void setStopReason(String v){stopReason=v;}
 public String getSummaryJson(){return summaryJson;} public void setSummaryJson(String v){summaryJson=v;}
}