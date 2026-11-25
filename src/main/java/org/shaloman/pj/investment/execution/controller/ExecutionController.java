package org.shaloman.pj.investment.execution.controller;
import java.util.List;
import org.shaloman.pj.common.api.ApiResponse;
import org.shaloman.pj.investment.execution.domain.ExecutionRecord;
import org.shaloman.pj.investment.execution.service.ExecutionService;
import org.springframework.web.bind.annotation.*;
/** 실행 이력 조회 API입니다. 예약/수동 실행 트리거는 아직 제공하지 않습니다. */
@RestController @RequestMapping("/api/executions")
public class ExecutionController {
 private final ExecutionService service;
 public ExecutionController(ExecutionService service){this.service=service;}
 @GetMapping public ApiResponse<List<ExecutionRecord>> recent(@RequestParam(defaultValue="50") int limit){return ApiResponse.success(service.recent(limit));}
 @GetMapping("/{id}") public ApiResponse<ExecutionRecord> byId(@PathVariable String id){return ApiResponse.success(service.byId(id));}
}