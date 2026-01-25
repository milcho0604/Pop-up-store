package com.store.popup.notification.exception;

import com.store.popup.common.exception.exceptionType.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FcmExceptionType implements ExceptionType {

    FCM_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 메시지 전송에 실패했습니다."),
    FCM_SEND_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 메시지 전송 중 인터럽트가 발생했습니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "회원의 FCM 토큰이 등록되어 있지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String message() {
        return message;
    }
}
