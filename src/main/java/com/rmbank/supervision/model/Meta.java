package com.rmbank.supervision.model;

import java.util.List;

import com.rmbank.supervision.common.utils.Page;

public class Meta extends Page {
    private Integer id;

    private Integer pid;

    private Integer level;

    private String path;

    private Integer leafed;

    private String name;

    private String key;

    private String description;

    private Integer used;
    
    private Integer configType;

    private String text;
    
    private List<Meta> children;
    
    public List<Meta> getChildren() {
		return children;
	}

	public void setChildren(List<Meta> children) {
		this.children = children;
	}

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

	private Integer childrenCount;
    
    private String state;
    
    public Integer getConfigType() {
		return configType;
	}

	public void setConfigType(Integer configType) {
		this.configType = configType;
	}

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

    public Integer getLeafed() {
        return leafed;
    }

    public void setLeafed(Integer leafed) {
        this.leafed = leafed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}