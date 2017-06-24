package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.GradeSchemeDetail;
import com.rmbank.supervision.model.ResourceConfig;

public interface GradeSchemeDetailService {
	 	int deleteByPrimaryKey(Integer id);

	    int insert(GradeSchemeDetail record);

	    int insertSelective(GradeSchemeDetail record);

	    GradeSchemeDetail selectByPrimaryKey(Integer id);

	    int updateByPrimaryKeySelective(GradeSchemeDetail record);

	    int updateByPrimaryKey(GradeSchemeDetail record);

		List<GradeSchemeDetail> getGradeSchemeDetailListByPid(
				GradeSchemeDetail gradeSchemeDetail);
		
		List<GradeSchemeDetail> getGradeSchemeDetailListByGradeId(
				GradeSchemeDetail gradeSchemeDetail);

		int getGradeSchemeDetailCount(GradeSchemeDetail gradeSchemeDetail);
		
		List<GradeSchemeDetail> getSchemeDetailListByPidAndGradeId(
				GradeSchemeDetail gradeSchemeDetail);

		List<GradeSchemeDetail> getGradeSchemeDetailList(
				GradeSchemeDetail schemeDetail);

		List<GradeSchemeDetail> getGradeSchemeDetailTreeByPid(
				GradeSchemeDetail schemeDetail);

		boolean saveOrUpdateDetail(GradeSchemeDetail detail);

		List<GradeSchemeDetail> getExistDetail(GradeSchemeDetail gsd);

		GradeSchemeDetail getGradeSchemeDetailById(Integer id);

		
}
