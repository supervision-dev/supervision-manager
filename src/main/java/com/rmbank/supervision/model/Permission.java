package com.rmbank.supervision.model;

import com.rmbank.supervision.common.utils.Page;

public class Permission extends Page{
    private Integer id;

    private String name;

    private Integer moudleId;

    private String description;

    private String fName;
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

    public Integer getMoudleId() {
        return moudleId;
    }

    public void setMoudleId(Integer moudleId) {
        this.moudleId = moudleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}
}