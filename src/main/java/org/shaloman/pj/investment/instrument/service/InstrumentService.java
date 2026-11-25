package org.shaloman.pj.investment.instrument.service;
import java.util.List;
import org.shaloman.pj.common.exception.ResourceNotFoundException;
import org.shaloman.pj.investment.instrument.domain.Instrument;
import org.shaloman.pj.investment.instrument.dto.*;
import org.shaloman.pj.investment.instrument.mapper.InstrumentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 종목 조회·등록·상태 변경의 업무 규칙입니다. */
@Service
public class InstrumentService {
 private final InstrumentMapper mapper;
 public InstrumentService(InstrumentMapper mapper){this.mapper=mapper;}
 @Transactional(readOnly=true) public List<Instrument> findAll(String market,String state){
  if(market!=null&&!market.matches("KR|US"))throw new IllegalArgumentException("marketCode는 KR 또는 US여야 합니다.");
  if(state!=null&&!state.matches("WATCH|ALLOW|EXCLUDE"))throw new IllegalArgumentException("watchState 값이 올바르지 않습니다.");
  return mapper.findAll(market,state);
 }
 @Transactional(readOnly=true) public Instrument findById(long id){Instrument x=mapper.findById(id);if(x==null)throw new ResourceNotFoundException("종목을 찾을 수 없습니다.");return x;}
 @Transactional public Instrument create(CreateInstrumentRequest r){
  Instrument x=new Instrument();x.setMarketCode(r.marketCode());x.setAssetType(r.assetType());x.setSymbol(r.symbol().trim().toUpperCase());x.setDisplayName(r.displayName().trim());
  x.setWatchState(r.watchState()==null?"WATCH":r.watchState());x.setEnabled(true);x.setMemo(r.memo());mapper.insert(x);return mapper.findById(x.getInstrumentId());
 }
 @Transactional public Instrument updateState(long id,UpdateInstrumentStateRequest r){Instrument x=findById(id);x.setWatchState(r.watchState());x.setEnabled(r.enabled());mapper.updateState(x);return mapper.findById(id);}
}