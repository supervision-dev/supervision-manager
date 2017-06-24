package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.RolePermission;

public interface RolePermissionService {
    int deleteByPrimaryKey(Integer id);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    RolePermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RolePermission record);

    int updateByPrimaryKey(RolePermission record);

	List<RolePermission> selectByRoleId(Integer roleId);

	int deleteByRoleId(Integer roleId);
}