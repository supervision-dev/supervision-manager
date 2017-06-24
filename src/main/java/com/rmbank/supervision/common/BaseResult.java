/**
 * copy right 2012 sctiyi all rights reserved
 * create time:3:55:59 PM
 * author:ftd
 */
package com.rmbank.supervision.common;

import java.io.Serializable;

/**
 * @author ftd
 *
 */
public class BaseResult implements Serializable {

	/** 变量 serialVersionUID(long) */
	private static final long serialVersionUID = 1L;

	private Integer code;
	private String message;
	private String gotoUrl;
	
	
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getGotoUrl() {
		return gotoUrl;
	}
	public void setGotoUrl(String gotoUrl) {
		this.gotoUrl = gotoUrl;
	}
	
	
}
