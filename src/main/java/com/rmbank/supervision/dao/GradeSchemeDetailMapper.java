package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.GradeSchemeDetail;
@MyBatisRepository
public interface GradeSchemeDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GradeSchemeDetail record);

    int insertSelective(GradeSchemeDetail record);

    GradeSchemeDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GradeSchemeDetail record);

    int updateByPrimaryKey(GradeSchemeDetail record);

	List<GradeSchemeDetail> getGradeSchemeDetailListByPid(
			GradeSchemeDetail gradeSchemeDetail);

	int getGradeSchemeDetailCount(GradeSchemeDetail gradeSchemeDetail);

	List<GradeSchemeDetail> getSchemeDetailListByPidAndGradeId(GradeSchemeDetail gradeSchemeDetail);

	List<GradeSchemeDetail> getGradeSchemeDetailList(
			GradeSchemeDetail schemeDetail);

	List<GradeSchemeDetail> getGradeSchemeDetailTreeByPid(
			GradeSchemeDetail schemeDetail);

	List<GradeSchemeDetail> getExistDetail(GradeSchemeDetail gsd);

	GradeSchemeDetail getGradeSchemeDetailById(Integer id);

	List<GradeSchemeDetail> getGradeSchemeDetailListByGradeId(
			GradeSchemeDetail gradeSchemeDetail);
}		