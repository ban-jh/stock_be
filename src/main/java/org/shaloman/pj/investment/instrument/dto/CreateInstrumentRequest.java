package org.shaloman.pj.investment.instrument.dto;
import jakarta.validation.constraints.*;
/** 주식 또는 ETF 종목을 목록에 등록할 때 사용하는 DTO입니다. */
public record CreateInstrumentRequest(@NotBlank @Pattern(regexp="KR|US") String marketCode,@NotBlank @Pattern(regexp="STOCK|ETF") String assetType,@NotBlank String symbol,@NotBlank String displayName,@Pattern(regexp="WATCH|ALLOW|EXCLUDE") String watchState,String memo){}