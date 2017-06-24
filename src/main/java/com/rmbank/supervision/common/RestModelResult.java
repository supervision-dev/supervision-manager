package com.rmbank.supervision.common;

import java.io.Serializable;

/**
 * Created by admin on 2016/9/18.
 */
public class RestModelResult<E> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3575327713107875249L;


    private int Code ;

    private int ApiCode;

    private String Message;

    private E Data;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getApiCode() {
        return ApiCode;
    }

    public void setApiCode(int apiCode) {
        ApiCode = apiCode;
    }

    public E getData() {
        return Data;
    }

    public void setData(E data) {
        Data = data;
    }
}
