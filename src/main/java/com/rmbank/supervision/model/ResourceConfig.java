package com.rmbank.supervision.model;

import com.rmbank.supervision.common.utils.Page;

public class ResourceConfig extends Page{
    private Integer id;

    private String name;

    private Integer moudleId;
    
    private Integer xtype;

    private String resource;
    
    private String parentName;
    
    private Integer parentId; 
    
    private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	//moudle_id对应的function的name
    private String functionName;
    
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

    public Integer getXtype() {
        return xtype;
    }

    public void setXtype(Integer xtype) {
        this.xtype = xtype;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Integer getMoudleId() {
		return moudleId;
	}

	public void setMoudleId(Integer moudleId) {
		this.moudleId = moudleId;
	}
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}