package org.shaloman.pj.ai.service;
import java.time.LocalDate;
import java.util.List;
import org.shaloman.pj.ai.domain.AiUsageDaily;
import org.shaloman.pj.ai.dto.RecordAiUsageRequest;
import org.shaloman.pj.ai.mapper.AiUsageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 사용량 원자 누적과 기간 조회를 담당합니다. 실제 AI 호출은 후속 단계에서 연결합니다. */
@Service
public class AiUsageService {
 private final AiUsageMapper mapper;
 public AiUsageService(AiUsageMapper mapper){this.mapper=mapper;}
 @Transactional(readOnly=true) public List<AiUsageDaily> findRange(LocalDate from,LocalDate to){
  if(from==null||to==null||to.isBefore(from))throw new IllegalArgumentException("from/to 날짜 범위가 올바르지 않습니다.");
  if(from.plusYears(1).isBefore(to))throw new IllegalArgumentException("한 번에 조회할 수 있는 기간은 최대 1년입니다.");
  return mapper.findRange(from,to);
 }
 @Transactional public void record(RecordAiUsageRequest r){
  AiUsageDaily row=new AiUsageDaily();row.setUsageDate(r.usageDate());row.setProviderCode(r.providerCode().trim());
  row.setCallCount(r.callCount());row.setInputTokens(r.inputTokens());row.setOutputTokens(r.outputTokens());row.setEstimatedCost(r.estimatedCost());
  mapper.addUsage(row);
 }
}