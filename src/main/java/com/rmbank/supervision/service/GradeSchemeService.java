package com.rmbank.supervision.service;
import java.util.List;

import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.ItemProcessGrade;
import com.rmbank.supervision.model.Role;



public interface GradeSchemeService {
	int deleteByPrimaryKey(Integer id);

    int insert(GradeScheme record);

    int insertSelective(GradeScheme record);

    GradeScheme selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GradeScheme record);

    int updateByPrimaryKey(GradeScheme record);
	
	List<GradeScheme> getGradeSchemeList(GradeScheme gradeScheme);

	int getGradeSchemeCount(GradeScheme gradeScheme);

	boolean saveOrUpdateScheme(GradeScheme scheme);

	List<GradeScheme> getExistGradeScheme(GradeScheme sch);

	List<GradeScheme> getGradeSchemeListASC(GradeScheme gradeScheme);

	void insertGradeList(List<ItemProcessGrade> gradeList);

	List<ItemProcessGrade> getGradeListByItemProcessId(Integer id);

	//int updeteState(Integer id);
	
}
