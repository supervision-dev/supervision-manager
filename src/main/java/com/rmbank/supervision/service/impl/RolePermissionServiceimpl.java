package com.rmbank.supervision.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.service.RolePermissionService;
@Service("rolePermissionService")
public class RolePermissionServiceimpl implements RolePermissionService {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(RolePermission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(RolePermission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RolePermission selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(RolePermission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(RolePermission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<RolePermission> selectByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
