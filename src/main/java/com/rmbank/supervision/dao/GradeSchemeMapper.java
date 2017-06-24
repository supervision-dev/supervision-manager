package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.Role;
@MyBatisRepository
public interface GradeSchemeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GradeScheme record);

    int insertSelective(GradeScheme record);

    GradeScheme selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GradeScheme record);

    int updateByPrimaryKey(GradeScheme record);
    
    List<GradeScheme> getGradeSchemeList(GradeScheme gradeScheme);

	int getGradeSchemeCount(GradeScheme gradeScheme);

	List<GradeScheme> getExistGradeScheme(GradeScheme gradeScheme);

	List<GradeScheme> getGradeSchemeListASC(GradeScheme gradeScheme);
}