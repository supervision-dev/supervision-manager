package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.RolePermissionMapper;
import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.model.UserOrgan;
import com.rmbank.supervision.service.RoleResourceService;

@Service("roleResourceService")
public class RoleResourceServiceimpl implements RoleResourceService {

	@Resource
	private RolePermissionMapper rolePermissionMapper;
	
	
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
	public boolean saveRoleResource(Integer roleId, Integer[] permissionIds) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try {
			rolePermissionMapper.deleteByRoleId(roleId);
			if(permissionIds!=null){
				for (Integer resId : permissionIds) {
					RolePermission rolePermission=new RolePermission();
					rolePermission.setId(0);
					rolePermission.setRoleId(roleId);
					rolePermission.setPermissionId(resId);
					rolePermission.setControl(1);
					rolePermissionMapper.insert(rolePermission);
				}
			}
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<RolePermission> selectByRoleId(Integer id) {
		// TODO Auto-generated method stub
		return rolePermissionMapper.selectByRoleId(id);
	}

	

}
