package org.shaloman.pj.investment.strategy.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.investment.strategy.domain.StrategyVersion;
/** 전략 버전 SQL 접근 계약입니다. */
@Mapper
public interface StrategyMapper {
 List<StrategyVersion> findAll();
 List<StrategyVersion> findByCode(@Param("strategyCode") String code);
 StrategyVersion findByCodeAndVersion(@Param("strategyCode") String code,@Param("versionNo") int version);
 int insert(StrategyVersion strategy);
 int disableEnabledVersions(@Param("strategyCode") String code);
 int enableVersion(@Param("strategyCode") String code,@Param("versionNo") int version);
}