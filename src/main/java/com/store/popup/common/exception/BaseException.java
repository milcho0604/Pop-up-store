package com.store.popup.common.exception;

import com.store.popup.common.exception.exceptionType.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException{
    private final ExceptionType exceptionType;

    @Override
    public String getMessage(){
        return exceptionType.message();
    }
}
