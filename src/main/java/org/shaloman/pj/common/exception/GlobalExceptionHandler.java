package org.shaloman.pj.common.exception;
import org.shaloman.pj.common.api.ApiResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.shaloman.pj.market.toss.exception.TossApiException;

/** 입력/중복/미존재/예상하지 못한 예외를 공통 API 형식으로 응답합니다. */
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ApiResponse<Void>> invalid(MethodArgumentNotValidException e){
  String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("요청값이 올바르지 않습니다.");
  return ResponseEntity.badRequest().body(ApiResponse.failure("INVALID_REQUEST",m));
 }
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<ApiResponse<Void>> badRequest(IllegalArgumentException e){return ResponseEntity.badRequest().body(ApiResponse.failure("INVALID_ARGUMENT",e.getMessage()));}
 @ExceptionHandler(DuplicateKeyException.class)
 public ResponseEntity<ApiResponse<Void>> duplicate(DuplicateKeyException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.failure("DUPLICATE_RESOURCE","같은 식별자의 데이터가 이미 존재합니다."));}
 @ExceptionHandler(ResourceNotFoundException.class)
 public ResponseEntity<ApiResponse<Void>> missing(ResourceNotFoundException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("NOT_FOUND",e.getMessage()));}
 @ExceptionHandler(TossApiException.class)
 public ResponseEntity<ApiResponse<Void>> tossFailure(TossApiException e){
  HttpStatus status=e.getHttpStatus()==429?HttpStatus.TOO_MANY_REQUESTS:HttpStatus.BAD_GATEWAY;
  ResponseEntity.BodyBuilder response=ResponseEntity.status(status);
  if(e.getHttpStatus()==429 && e.getRetryAfter()!=null && !e.getRetryAfter().isBlank()) response.header(HttpHeaders.RETRY_AFTER,e.getRetryAfter());
  return response.body(ApiResponse.failure(e.getErrorCode(),e.getMessage()));
 }
 @ExceptionHandler(Exception.class)
 public ResponseEntity<ApiResponse<Void>> unexpected(Exception e){return ResponseEntity.internalServerError().body(ApiResponse.failure("INTERNAL_ERROR","요청 처리 중 오류가 발생했습니다."));}
}
