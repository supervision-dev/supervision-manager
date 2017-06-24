package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.ItemProcessGrade;
@MyBatisRepository
public interface ItemProcessGradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemProcessGrade record);

    int insertSelective(ItemProcessGrade record);

    ItemProcessGrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemProcessGrade record);

    int updateByPrimaryKey(ItemProcessGrade record);

	void insertGradeList(List<ItemProcessGrade> gradeList);

	List<ItemProcessGrade> getGradeListByItemProcessId(Integer id);
}