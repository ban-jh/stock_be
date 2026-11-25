package org.shaloman.pj.investment.instrument.controller;
import jakarta.validation.Valid;
import java.util.List;
import org.shaloman.pj.common.api.ApiResponse;
import org.shaloman.pj.investment.instrument.domain.Instrument;
import org.shaloman.pj.investment.instrument.dto.*;
import org.shaloman.pj.investment.instrument.service.InstrumentService;
import org.springframework.web.bind.annotation.*;
/** 투자 후보와 매매 허용 종목을 관리하는 REST API입니다. */
@RestController @RequestMapping("/api/instruments")
public class InstrumentController {
 private final InstrumentService service;
 public InstrumentController(InstrumentService service){this.service=service;}
 @GetMapping public ApiResponse<List<Instrument>> all(@RequestParam(required=false) String marketCode,@RequestParam(required=false) String watchState){return ApiResponse.success(service.findAll(marketCode,watchState));}
 @GetMapping("/{id}") public ApiResponse<Instrument> byId(@PathVariable long id){return ApiResponse.success(service.findById(id));}
 @PostMapping public ApiResponse<Instrument> create(@Valid @RequestBody CreateInstrumentRequest r){return ApiResponse.success(service.create(r));}
 @PatchMapping("/{id}/state") public ApiResponse<Instrument> state(@PathVariable long id,@Valid @RequestBody UpdateInstrumentStateRequest r){return ApiResponse.success(service.updateState(id,r));}
}