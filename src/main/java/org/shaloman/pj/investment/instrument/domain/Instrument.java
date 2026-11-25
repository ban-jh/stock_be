package org.shaloman.pj.investment.instrument.domain;
import java.time.LocalDateTime;
/** 종목 허용 목록의 DB 모델입니다. */
public class Instrument {
 private Long instrumentId; private String marketCode; private String assetType; private String symbol; private String displayName; private String watchState; private Boolean enabled; private String memo; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 public Long getInstrumentId(){return instrumentId;} public void setInstrumentId(Long v){instrumentId=v;}
 public String getMarketCode(){return marketCode;} public void setMarketCode(String v){marketCode=v;}
 public String getAssetType(){return assetType;} public void setAssetType(String v){assetType=v;}
 public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v;}
 public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;}
 public String getWatchState(){return watchState;} public void setWatchState(String v){watchState=v;}
 public Boolean getEnabled(){return enabled;} public void setEnabled(Boolean v){enabled=v;}
 public String getMemo(){return memo;} public void setMemo(String v){memo=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
 public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}