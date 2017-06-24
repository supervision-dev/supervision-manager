package com.rmbank.supervision.model;

public class ItemProcessGrade {
    private Integer id;

    private Integer itemProcessId;
    
    private Integer gradeSchemeId;

    private Integer gradeDetailId;

    private Double grade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemProcessId() {
        return itemProcessId;
    }

    public void setItemProcessId(Integer itemProcessId) {
        this.itemProcessId = itemProcessId;
    }

    public Integer getGradeDetailId() {
        return gradeDetailId;
    }

    public void setGradeDetailId(Integer gradeDetailId) {
        this.gradeDetailId = gradeDetailId;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

	public Integer getGradeSchemeId() {
		return gradeSchemeId;
	}

	public void setGradeSchemeId(Integer gradeSchemeId) {
		this.gradeSchemeId = gradeSchemeId;
	}
}