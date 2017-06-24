package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.PermissionMapper;
import com.rmbank.supervision.dao.PermissionResourceMapper;
import com.rmbank.supervision.model.PermissionResource;
import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.service.PermissionResourceService;

@Service("PermissionResourceService") 
public class PermissionResourceServiceimpl implements PermissionResourceService {

	@Resource
	private PermissionResourceMapper permissionResourceMapper;
	
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(PermissionResource record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(PermissionResource record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PermissionResource selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(PermissionResource record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(PermissionResource record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean savePermissionResource(Integer permissionId,
			Integer[] resourceIds) {
		
		// TODO Auto-generated method stub
				boolean isSuccess = false;
				try {
					permissionResourceMapper.deleteByPermissionId(permissionId);
					if(resourceIds!=null){
						for (Integer resId : resourceIds) {
							PermissionResource permissionResource=new PermissionResource();
							permissionResource.setId(0);
							permissionResource.setPermissionId(permissionId);
							permissionResource.setResourceId(resId);
							permissionResourceMapper.insert(permissionResource);
						}
						PermissionResource permissionResource=new PermissionResource();
						permissionResource.setId(0);
						permissionResource.setPermissionId(permissionId);
						permissionResource.setResourceId(66);
						permissionResourceMapper.insert(permissionResource);
						permissionResource=new PermissionResource();
						permissionResource.setId(0);
						permissionResource.setPermissionId(permissionId);
						permissionResource.setResourceId(67);
						permissionResourceMapper.insert(permissionResource);
					}
					isSuccess = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return isSuccess;
	}

	@Override
	public List<PermissionResource> selectByPermissionId(Integer id) {
		// TODO Auto-generated method stub
		return permissionResourceMapper.selectByPermissionId(id);
	}



}
