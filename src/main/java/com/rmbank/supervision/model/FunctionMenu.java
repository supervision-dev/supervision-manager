package com.rmbank.supervision.model;

import java.util.List;

import com.rmbank.supervision.common.utils.Page;

public class FunctionMenu extends Page{
    private Integer id;
    
    private String name;

    private Integer parentId;

    private Integer leaf;

    private String url;

    private Integer sort;

    private Integer used;
    
    private List<FunctionMenu> children;
    
	private Integer childrenCount;
    
    private String state;
    
    private String text;

    private int selected;
    
    private List<FunctionMenu> childMenulist; 
    
    public Integer getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(Integer childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

   
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLeaf() {
        return leaf;
    }

    public void setLeaf(Integer leaf) {
        this.leaf = leaf;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

	public List<FunctionMenu> getChildMenulist() {
		return childMenulist;
	}

	public void setChildMenulist(List<FunctionMenu> childMenulist) {
		this.childMenulist = childMenulist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FunctionMenu> getChildren() {
		return children;
	}

	public void setChildren(List<FunctionMenu> children) {
		this.children = children;
	}
}