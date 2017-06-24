package com.rmbank.supervision.model;

import java.util.List;

import com.rmbank.supervision.common.utils.Page;

public class GradeSchemeDetail extends Page{
    private Integer id;

    private Integer gradeId;

    private Integer pid;

    private Integer level;

    private String path;

    private Integer leafed;

    private String name;

    private Double grade;
    
    private String schemaName;
    
    private String pName;
    
    private String ppName;
    
    
    private Integer childrenCount;
    
    private String state;
    
    private String text;
    
    private List<GradeSchemeDetail> children;
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

	public List<GradeSchemeDetail> getChildren() {
		return children;
	}

	public void setChildren(List<GradeSchemeDetail> children) {
		this.children = children;
	}    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
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

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getPpName() {
		return ppName;
	}

	public void setPpName(String ppName) {
		this.ppName = ppName;
	}
}