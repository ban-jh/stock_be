package org.shaloman.pj.common.exception;
/** API 조회 대상이 없을 때 404로 변환합니다. */
public class ResourceNotFoundException extends RuntimeException {
 public ResourceNotFoundException(String message){super(message);}
}