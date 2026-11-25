package org.shaloman.pj.investment.instrument.mapper;
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.shaloman.pj.investment.instrument.domain.Instrument;
/** 종목 테이블의 SQL 접근 계약입니다. */
@Mapper
public interface InstrumentMapper {
 List<Instrument> findAll(@Param("marketCode") String marketCode,@Param("watchState") String watchState);
 Instrument findById(@Param("instrumentId") long instrumentId);
 int insert(Instrument instrument);
 int updateState(Instrument instrument);
}