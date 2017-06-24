package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Role;
@MyBatisRepository
public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

	List<Role> getRoleList(Role role);

	int getRoleCount(Role role);

	Role getRoleById(Integer id);

	List<Role> getExistRole(Role role);

	void deleteRoleById(Integer id);
}