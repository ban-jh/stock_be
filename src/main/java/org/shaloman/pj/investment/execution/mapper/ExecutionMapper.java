package org.shaloman.pj.investment.execution.mapper;
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.shaloman.pj.investment.execution.domain.ExecutionRecord;
/** 실행 이력 조회 SQL 접근 계약입니다. 실행을 생성하는 기능은 후속 단계에서 추가합니다. */
@Mapper
public interface ExecutionMapper {
 List<ExecutionRecord> findRecent(@Param("limit") int limit);
 ExecutionRecord findById(@Param("executionId") String executionId);
}