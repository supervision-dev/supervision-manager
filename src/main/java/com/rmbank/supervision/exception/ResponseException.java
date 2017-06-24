package com.rmbank.supervision.exception;

/**
 * Created by sam on 2016/3/25.
 */
public class ResponseException extends BusinessException {
    public ResponseException(ResponseExceptionEnum exceptionInfo) {
        super(exceptionInfo);
    }
}
