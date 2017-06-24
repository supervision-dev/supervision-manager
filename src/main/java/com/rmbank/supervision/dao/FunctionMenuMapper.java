package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.FunctionMenu;
@MyBatisRepository
public interface FunctionMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FunctionMenu record);

    int insertSelective(FunctionMenu record);

    FunctionMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FunctionMenu record);

    int updateByPrimaryKey(FunctionMenu record);

	List<FunctionMenu> getFunctionMenuByCondition(FunctionMenu functionMenu);

	List<FunctionMenu> getFunctionMenuByParentId(FunctionMenu functionMenu);

	List<FunctionMenu> getFunctionMenuList(FunctionMenu functionMenu);

	FunctionMenu getFunctionMenusById(Integer id);

	List<FunctionMenu> getFunctionByUserId(Integer userId);
}