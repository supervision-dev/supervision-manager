/**
   * Copy Right Info		  : 鍥涘窛澶╃考缃戠粶鏈嶅姟鏈夐檺璐ｄ换鍏徃鐗堟潈锟�锟斤拷 
   * Project                  : 澶╃考锟�锟斤拷妗嗘灦
   * File name                : CommonActionException.java
   * Version                  : 
   * Create time     		  : 2013-9-30
   * Modify History           : 
   * Description   鏂囦欢鎻忚堪銆傦拷?锟�
   * 
   **/
package com.rmbank.supervision.common.exception;

/**
 * @author goulin
 * @Descripntion	Web Action鎵ц寮傚父锟�
 */
public class CommonActionException extends RuntimeException {
	private String detail;
	
	

	public CommonActionException() {
		super();
		// TODO Auto-generated constructor stub
	}



	public CommonActionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}



	public CommonActionException(Throwable cause) {
		super(cause);
	}

	
	/**
	 * @param message	寮傚父锟�锟斤拷鎻忚堪
	 * @param detail	寮傚父璇︾粏淇℃伅
	 */
	public CommonActionException(String message, String detail) {
		super(message);
		this.detail = detail;
	}



	/**
	 * 
	 * @return 寮傚父璇︾粏淇℃伅
	 */
	public String getDetail() {
		return detail;
	}



	/**
	 * @param detail	寮傚父璇︾粏淇℃伅
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	

}
