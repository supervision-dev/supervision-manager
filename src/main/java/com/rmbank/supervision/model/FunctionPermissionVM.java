package com.rmbank.supervision.model;

import java.util.List;

public class FunctionPermissionVM {
	private Integer id;//所属模块Id；
	private String name;//所属模块名称；
	private List<Permission> itemList;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Permission> getItemList() {
		return itemList;
	}
	public void setItemList(List<Permission> itemList) {
		this.itemList = itemList;
	}
}
