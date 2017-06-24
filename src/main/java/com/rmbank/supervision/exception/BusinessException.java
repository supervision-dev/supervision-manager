package com.rmbank.supervision.exception;

/**
 * 业务异常，建议所有的业务异常从本异常扩展
 * Created by sam on 2016/3/21.
 */
public class BusinessException extends Exception {
    private IExceptionEnumRoot exceptionInfo;

    public BusinessException(IExceptionEnumRoot exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public IExceptionEnumRoot getExceptionInfo() {
        return exceptionInfo;
    }
    public BusinessException(String msg){super(msg);}
}
