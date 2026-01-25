package com.store.popup.notification.exception;

import com.store.popup.common.exception.BaseException;

public class FcmException extends BaseException {

    public FcmException(FcmExceptionType exceptionType) {
        super(exceptionType);
    }

    public static FcmException sendFailed() {
        return new FcmException(FcmExceptionType.FCM_SEND_FAILED);
    }

    public static FcmException sendInterrupted() {
        return new FcmException(FcmExceptionType.FCM_SEND_INTERRUPTED);
    }

    public static FcmException tokenNotFound() {
        return new FcmException(FcmExceptionType.FCM_TOKEN_NOT_FOUND);
    }
}
