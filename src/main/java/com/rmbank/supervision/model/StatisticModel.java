package com.rmbank.supervision.model;

public class StatisticModel {
	private Integer id;
	private Integer orgId;
	private Integer status;
	private String orgName;
	private Integer totalCount;
	private Integer comCount;
	private Integer unComCount;
	public Integer getOrgId() {
		return orgId;
	}
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getComCount() {
		return comCount;
	}
	public void setComCount(Integer comCount) {
		this.comCount = comCount;
	}
	public Integer getUnComCount() {
		return unComCount;
	}
	public void setUnComCount(Integer unComCount) {
		this.unComCount = unComCount;
	}
	public Integer getOverUnComCount() {
		return overUnComCount;
	}
	public void setOverUnComCount(Integer overUnComCount) {
		this.overUnComCount = overUnComCount;
	}
	public Integer getOverComCount() {
		return overComCount;
	}
	public void setOverComCount(Integer overComCount) {
		this.overComCount = overComCount;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	private Integer overUnComCount;
	private Integer overComCount;
	
}
