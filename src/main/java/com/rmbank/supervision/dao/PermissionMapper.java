package com.rmbank.supervision.dao;


import java.util.List;

import com.rmbank.supervision.model.Permission;
@MyBatisRepository
public interface PermissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);

	List<Permission> getPermissionList(Permission permission);

	int getPermissionCount(Permission permission);

	List<Permission> getExistPermission(Permission per);
}