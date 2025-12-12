package com.store.popup.common.exception;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.exception.exceptionType.ExceptionType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ExceptionType exceptionType = e.getExceptionType();

        return ResponseEntity.status(exceptionType.httpStatus())
                .body(ErrorResponse.of(exceptionType));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonErrorDto> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonErrorDto> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonErrorDto(HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorDto> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommonErrorDto(HttpStatus.BAD_REQUEST, "요청 처리 중 오류가 발생했습니다: " + e.getMessage()));
    }
}
