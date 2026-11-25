package org.shaloman.pj.ai.controller;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.shaloman.pj.ai.domain.AiUsageDaily;
import org.shaloman.pj.ai.dto.RecordAiUsageRequest;
import org.shaloman.pj.ai.service.AiUsageService;
import org.shaloman.pj.common.api.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
/** AI 사용량 집계를 기록하고 기간별 현황을 조회합니다. */
@RestController
@RequestMapping("/api/ai-usage")
public class AiUsageController {
 private final AiUsageService service;
 public AiUsageController(AiUsageService service){this.service=service;}
 @GetMapping public ApiResponse<List<AiUsageDaily>> range(
  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){
  return ApiResponse.success(service.findRange(from,to));
 }
 @PostMapping public ApiResponse<Void> record(@Valid @RequestBody RecordAiUsageRequest request){
  service.record(request);return ApiResponse.success(null);
 }
}