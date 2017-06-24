package com.rmbank.supervision.model;

import java.util.List;

public class OrganVM {
	
	private Integer id;
	private String name;
	private List<Organ> itemList;
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
	public List<Organ> getItemList() {
		return itemList;
	}
	public void setItemList(List<Organ> itemList) {
		this.itemList = itemList;
	}
}
