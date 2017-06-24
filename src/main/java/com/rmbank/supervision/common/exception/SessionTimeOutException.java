/**
 * copy right 2012 sctiyi all rights reserved
 * create time:下午03:27:38
 * author:ftd
 */
package com.rmbank.supervision.common.exception;

/**
 * @author ftd
 * 登录超时异常
 *
 */
public class SessionTimeOutException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7412498327194139091L;
	protected Object[] values = null;
	
	public SessionTimeOutException(String msg){
		super(msg);
	}
	
	public SessionTimeOutException(String msg,String value){
		super(msg);
		this.values = new Object[]{value};
	}
	
	public SessionTimeOutException(String msg,Object[] values){
		super(msg);
		this.values = values;
	}
	
	public SessionTimeOutException(String msg, String value,Throwable e){
		super(msg,e);
		this.values = new Object[]{value};
	}
	
	public SessionTimeOutException(String msg, Object[] values,Throwable e){
		super(msg,e);
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}
	
	

}
