package org.shaloman.pj.investment.strategy.service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;
import org.shaloman.pj.common.exception.ResourceNotFoundException;
import org.shaloman.pj.investment.strategy.domain.StrategyVersion;
import org.shaloman.pj.investment.strategy.dto.CreateStrategyVersionRequest;
import org.shaloman.pj.investment.strategy.mapper.StrategyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 전략 버전 생성과 PAPER 버전 전환 규칙을 한곳에서 적용합니다. */
@Service
public class StrategyService {
 private final StrategyMapper mapper; private final JsonMapper json;
 public StrategyService(StrategyMapper mapper,JsonMapper json){this.mapper=mapper;this.json=json;}
 @Transactional(readOnly=true) public List<StrategyVersion> findAll(){return mapper.findAll();}
 @Transactional(readOnly=true) public List<StrategyVersion> findByCode(String code){
  List<StrategyVersion> rows=mapper.findByCode(code); if(rows.isEmpty())throw new ResourceNotFoundException("전략을 찾을 수 없습니다."); return rows;
 }
 @Transactional public StrategyVersion create(CreateStrategyVersionRequest r){
  if(mapper.findByCodeAndVersion(r.strategyCode(),r.versionNo())!=null)throw new IllegalArgumentException("이미 존재하는 전략 버전입니다.");
  StrategyVersion s=new StrategyVersion(); s.setStrategyCode(r.strategyCode().trim()); s.setVersionNo(r.versionNo()); s.setStrategyName(r.strategyName().trim());
  s.setEnabled(false); s.setExecutionMode("PAPER");
  try{s.setConfigJson(json.writeValueAsString(r.config()));}catch(JacksonException e){throw new IllegalArgumentException("전략 설정 JSON 변환에 실패했습니다.");}
  mapper.insert(s); return mapper.findByCodeAndVersion(s.getStrategyCode(),s.getVersionNo());
 }
 @Transactional public StrategyVersion activatePaper(String code,int version){
  if(mapper.findByCodeAndVersion(code,version)==null)throw new ResourceNotFoundException("전략 버전을 찾을 수 없습니다.");
  mapper.disableEnabledVersions(code); mapper.enableVersion(code,version); return mapper.findByCodeAndVersion(code,version);
 }
}
