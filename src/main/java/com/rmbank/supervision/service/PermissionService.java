package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Permission;

public interface PermissionService {
	int deleteByPrimaryKey(Integer id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);

	List<Permission> getPermissionList(Permission permission);

	int getPermissionCount(Permission permission);

	boolean saveOrUpdatePermission(Permission permission);

	List<Permission> getExistPermission(Permission per);

	boolean deletePermissionById(Integer id);
}
