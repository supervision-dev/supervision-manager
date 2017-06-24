package com.rmbank.supervision.model;

import java.util.List;

import com.rmbank.supervision.common.utils.Page;

public class Organ extends Page{
    private Integer id;

    private Integer pid;

    private Integer level;

    private String path;

    private Integer leaf;

    private String name;

    private Integer sort;

    private Integer used;
    
    private Integer supervision;
    
    private Integer orgtype;
    
    //Org表的pid对应的org表的name
    private String parentName;
    
    
    private List<Organ> children;
    
    private Integer childrenCount;
    
    private String state;
    
    private String text;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

	public Integer getSupervision() {
		return supervision;
	}

	public void setSupervision(Integer supervision) {
		this.supervision = supervision;
	}

	public List<Organ> getChildren() {
		return children;
	}

	public void setChildren(List<Organ> children) {
		this.children = children;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(Integer childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Integer getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(Integer orgtype) {
		this.orgtype = orgtype;
	}
}