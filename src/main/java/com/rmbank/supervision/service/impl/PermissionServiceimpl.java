package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.PermissionMapper;
import com.rmbank.supervision.model.Permission;
import com.rmbank.supervision.service.PermissionService;

@Service("PermissionService")
public class PermissionServiceimpl implements PermissionService {
	@Resource
	private PermissionMapper permissionMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(Permission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(Permission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Permission selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return permissionMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Permission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Permission record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Permission> getPermissionList(Permission permission) {
		// TODO Auto-generated method stub
		return permissionMapper.getPermissionList(permission);
	}

	@Override
	public int getPermissionCount(Permission permission) {
		// TODO Auto-generated method stub
		return permissionMapper.getPermissionCount(permission);
	}

	@Override
	public boolean saveOrUpdatePermission(Permission permission) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(permission.getId()>0){
				permissionMapper.updateByPrimaryKeySelective(permission);
				isSuccess = true;
			}else{	               
				permissionMapper.insert(permission); 
				isSuccess = true;
         
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<Permission> getExistPermission(Permission per) {
		// TODO Auto-generated method stub
		return permissionMapper.getExistPermission(per);
	}

	@Override
	public boolean deletePermissionById(Integer id) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try{
			permissionMapper.deleteByPrimaryKey(id);
			isSuccess=true;			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}
	
	
}
