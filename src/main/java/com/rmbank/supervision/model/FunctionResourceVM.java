package com.rmbank.supervision.model;

import java.util.List;

public class FunctionResourceVM {
	private Integer id;//所属模块Id；
	private String name;//所属模块名称；
	private List<ResourceConfig> itemList;
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
	public List<ResourceConfig> getItemList() {
		return itemList;
	}
	public void setItemList(List<ResourceConfig> itemList) {
		this.itemList = itemList;
	}
}
