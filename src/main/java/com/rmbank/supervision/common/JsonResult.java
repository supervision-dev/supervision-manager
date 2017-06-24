/**
 * copy right 2012 sctiyi all rights reserved
 * create time:下午05:37:06
 * author:ftd
 */
package com.rmbank.supervision.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ftd
 * @param <E>
 *
 */
public class JsonResult<E> extends BaseResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3575327713107875249L;

	private E obj;
	
	private List<E> list =new ArrayList<E>();

	public E getObj() {
		return obj;
	}

	public void setObj(E obj) {
		this.obj = obj;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}
	
	
}
