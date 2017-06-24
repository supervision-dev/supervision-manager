package com.rmbank.supervision.common;

import java.io.Serializable;

public class ReturnResult<T>
  implements Serializable
{
  private static final long serialVersionUID = -4459853194783840244L;
  private Integer code;
  private String message;
  private T resultObject;
  public static final int SUCCESS = 1;
  public static final int FAILURE = 0;

  public Integer getCode()
  {
    return this.code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getResultObject() {
    return this.resultObject;
  }

  public void setResultObject(T result) {
    this.resultObject = result;
  }

  public boolean succeed()
  {
    return this.code.intValue() == 1;
  }
}