package com.rmbank.supervision.common;

import java.util.List;

public class DataListResult<E> {

	public List<E> datalist;
	public E data;
	public Integer loginOrganRoleType; 
	public List<E> getDatalist() {
		return datalist;
	}
	public Integer getLoginOrganRoleType() {
		return loginOrganRoleType;
	}
	public void setLoginOrganRoleType(Integer loginOrganRoleType) {
		this.loginOrganRoleType = loginOrganRoleType;
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
