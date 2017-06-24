package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.PermissionResource;
import com.rmbank.supervision.model.RolePermission;

public interface PermissionResourceService {
	int deleteByPrimaryKey(Integer id);

    int insert(PermissionResource record);

    int insertSelective(PermissionResource record);

    PermissionResource selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PermissionResource record);

    int updateByPrimaryKey(PermissionResource record);

	boolean savePermissionResource(Integer permissionId, Integer[] resourceIds);


	List<PermissionResource> selectByPermissionId(Integer id);
}
