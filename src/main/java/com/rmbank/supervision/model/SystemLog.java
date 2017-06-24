package com.rmbank.supervision.model;

import java.util.Date;

import com.rmbank.supervision.common.utils.Page;

public class SystemLog extends Page {
    private Integer id;

    private Integer moudleId;

    private String tableName;

    private Integer rowId;

    private Integer operation;

    private String description;

    private Integer orgId;
    
    private String orgName;

    private Integer operId;
    
    private String operName;

    private Date operTime;
    
    private String operTimes;

    public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getOperTimes() {
		return operTimes;
	}

	public void setOperTimes(String operTimes) {
		this.operTimes = operTimes;
	}

	private String ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMoudleId() {
        return moudleId;
    }

    public void setMoudleId(Integer moudleId) {
        this.moudleId = moudleId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getOperId() {
        return operId;
    }

    public void setOperId(Integer operId) {
        this.operId = operId;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}