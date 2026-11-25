package org.shaloman.pj.ai.mapper;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.ai.domain.AiUsageDaily;
/** AI 사용량 집계 SQL 접근 계약입니다. */
@Mapper
public interface AiUsageMapper {
 List<AiUsageDaily> findRange(@Param("from") LocalDate from,@Param("to") LocalDate to);
 int addUsage(AiUsageDaily usage);
}