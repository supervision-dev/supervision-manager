package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.PermissionResource;
@MyBatisRepository
public interface PermissionResourceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PermissionResource record);

    int insertSelective(PermissionResource record);

    PermissionResource selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PermissionResource record);

    int updateByPrimaryKey(PermissionResource record);

	int deleteByPermissionId(Integer permissionId);

	List<PermissionResource> selectByPermissionId(Integer id);
}