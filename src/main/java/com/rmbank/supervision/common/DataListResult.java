package com.rmbank.supervision.common;

import java.util.List;

public class DataListResult<E> {

	public List<E> datalist;
	public E data;
	public List<E> getDatalist() {
		return datalist;
	}
	public void setDatalist(List<E> datalist) {
		this.datalist = datalist;
	}
	public E getData() {
		return data;
	}
	public void setData(E data) {
		this.data = data;
	}
	
}
