package org.shaloman.pj.investment.execution.service;
import java.util.List;
import org.shaloman.pj.common.exception.ResourceNotFoundException;
import org.shaloman.pj.investment.execution.domain.ExecutionRecord;
import org.shaloman.pj.investment.execution.mapper.ExecutionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 실행 이력을 조회하고 화면 요청 건수를 안전한 범위로 제한합니다. */
@Service
public class ExecutionService {
 private final ExecutionMapper mapper;
 public ExecutionService(ExecutionMapper mapper){this.mapper=mapper;}
 @Transactional(readOnly=true) public List<ExecutionRecord> recent(int requested){return mapper.findRecent(Math.max(1,Math.min(requested,200)));}
 @Transactional(readOnly=true) public ExecutionRecord byId(String id){ExecutionRecord x=mapper.findById(id);if(x==null)throw new ResourceNotFoundException("실행 기록을 찾을 수 없습니다.");return x;}
}