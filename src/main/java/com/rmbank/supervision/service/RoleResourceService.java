package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.RolePermission;

public interface RoleResourceService {
	int deleteByPrimaryKey(Integer id);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    RolePermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RolePermission record);

    int updateByPrimaryKey(RolePermission record);

	boolean saveRoleResource(Integer roleId, Integer[] resourceIds);

	List<RolePermission> selectByRoleId(Integer id);

	//List<RoleResource> getRoleResourceListByRoleId(Integer id);
	

}
