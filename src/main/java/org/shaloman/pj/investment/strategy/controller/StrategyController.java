package org.shaloman.pj.investment.strategy.controller;
import jakarta.validation.Valid;
import java.util.List;
import org.shaloman.pj.common.api.ApiResponse;
import org.shaloman.pj.investment.strategy.domain.StrategyVersion;
import org.shaloman.pj.investment.strategy.dto.CreateStrategyVersionRequest;
import org.shaloman.pj.investment.strategy.service.StrategyService;
import org.springframework.web.bind.annotation.*;
/** 투자 전략 버전 조회, 생성, PAPER 활성화를 제공합니다. */
@RestController @RequestMapping("/api/strategies")
public class StrategyController {
 private final StrategyService service;
 public StrategyController(StrategyService service){this.service=service;}
 @GetMapping public ApiResponse<List<StrategyVersion>> all(){return ApiResponse.success(service.findAll());}
 @GetMapping("/{code}/versions") public ApiResponse<List<StrategyVersion>> byCode(@PathVariable String code){return ApiResponse.success(service.findByCode(code));}
 @PostMapping("/versions") public ApiResponse<StrategyVersion> create(@Valid @RequestBody CreateStrategyVersionRequest request){return ApiResponse.success(service.create(request));}
 @PutMapping("/{code}/versions/{version}/activate") public ApiResponse<StrategyVersion> activate(@PathVariable String code,@PathVariable int version){return ApiResponse.success(service.activatePaper(code,version));}
}