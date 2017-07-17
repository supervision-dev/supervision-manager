package com.rmbank.supervision.common;

import javax.servlet.http.HttpServletResponse;

public class ExportExcelVO {
	private String fileName;
	private String orgName;
	private String title;
	private StatisticModelList sml;
	private HttpServletResponse response;
	private String itemTdA;
	public String getItemTdA() {
		return itemTdA;
	}
	public void setItemTdA(String itemTdA) {
		this.itemTdA = itemTdA;
	}
	public String getItemTdB() {
		return itemTdB;
	}
	public void setItemTdB(String itemTdB) {
		this.itemTdB = itemTdB;
	}
	private String itemTdB;
	
	
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public StatisticModelList getSml() {
		return sml;
	}
	public void setSml(StatisticModelList sml) {
		this.sml = sml;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	
}
