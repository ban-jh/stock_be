package org.shaloman.pj.investment.instrument.dto;
import jakarta.validation.constraints.*;
/** 종목의 관심/허용/제외 상태만 변경합니다. */
public record UpdateInstrumentStateRequest(@NotBlank @Pattern(regexp="WATCH|ALLOW|EXCLUDE") String watchState,boolean enabled){}