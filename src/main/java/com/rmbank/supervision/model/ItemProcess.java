package com.rmbank.supervision.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class ItemProcess {
    private Integer id;
    
    private String uuid;

    private Integer itemId;

    private Boolean defined;

    private Integer contentTypeId;

    private Integer orgId;

    private String last;

    private String content;

    private String changeContent;

    private Integer preparerOrgId;

    private Integer preparerId;
    
    /**
     * 量化节点分值
     */
    private Integer gradeSchemeId;
    
    private String values;
    
    private String detailId;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preparerTime;
    
    private String preparerTimes;
    
    /**
     * 定义是否量化字段， 只用作前台使用；
     */
    private Integer isValue;
    
    private Integer tag;
    
    
    /**
     * 定义是否完结，跟踪字段， 只用作前台使用；
     */
    private Integer isOver;
    
    private Integer isOverStatus;
    
    
    private Integer isFollowStatus;
    
    /**
     * 量化选择模型
     */
    private Integer valueTypeId;
    /**
     * 量化分值
     */
    private Double valueTypeValue; 
    
    private List<ItemProcessFile> fileList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Boolean getDefined() {
        return defined;
    }

    public void setDefined(Boolean defined) {
        this.defined = defined;
    }

    public Integer getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(Integer contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPreparerOrgId() {
        return preparerOrgId;
    }

    public void setPreparerOrgId(Integer preparerOrgId) {
        this.preparerOrgId = preparerOrgId;
    }

    public Integer getPreparerId() {
        return preparerId;
    }

    public void setPreparerId(Integer preparerId) {
        this.preparerId = preparerId;
    }

    public Date getPreparerTime() {
        return preparerTime;
    }

    public void setPreparerTime(Date preparerTime) {
        this.preparerTime = preparerTime;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getIsValue() {
		return isValue;
	}

	public void setIsValue(Integer isValue) {
		this.isValue = isValue;
	}

	public List<ItemProcessFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<ItemProcessFile> fileList) {
		this.fileList = fileList;
	}

	public String getPreparerTimes() {
		return preparerTimes;
	}

	public void setPreparerTimes(String preparerTimes) {
		this.preparerTimes = preparerTimes;
	}

	public Double getValueTypeValue() {
		return valueTypeValue;
	}

	public void setValueTypeValue(Double valueTypeValue) {
		this.valueTypeValue = valueTypeValue;
	}

	public Integer getValueTypeId() {
		return valueTypeId;
	}

	public void setValueTypeId(Integer valueTypeId) {
		this.valueTypeId = valueTypeId;
	}

	public Integer getIsOver() {
		return isOver;
	}

	public void setIsOver(Integer isOver) {
		this.isOver = isOver;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	public Integer getIsOverStatus() {
		return isOverStatus;
	}

	public void setIsOverStatus(Integer isOverStatus) {
		this.isOverStatus = isOverStatus;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public Integer getIsFollowStatus() {
		return isFollowStatus;
	}

	public void setIsFollowStatus(Integer isFollowStatus) {
		this.isFollowStatus = isFollowStatus;
	}

	public String getChangeContent() {
		return changeContent;
	}

	public void setChangeContent(String changeContent) {
		this.changeContent = changeContent;
	}

	public Integer getGradeSchemeId() {
		return gradeSchemeId;
	}

	public void setGradeSchemeId(Integer gradeSchemeId) {
		this.gradeSchemeId = gradeSchemeId;
	}
}