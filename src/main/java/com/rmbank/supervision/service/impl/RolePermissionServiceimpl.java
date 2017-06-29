package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.PermissionResourceMapper;
import com.rmbank.supervision.dao.RolePermissionMapper;
import com.rmbank.supervision.model.PermissionResource;
import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.service.RolePermissionService;
@Service("rolePermissionService")
public class RolePermissionServiceimpl implements RolePermissionService {


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
	public List<RolePermission> selectByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		return rolePermissionMapper.selectByRoleId(roleId);
	}

	@Override
	public int deleteByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		return rolePermissionMapper.deleteByRoleId(roleId);
	}

	@Override
	public void saveRolePermission(Integer roleId, List<Integer> idList) {
		// TODO Auto-generated method stub 
		try {
			rolePermissionMapper.deleteByRoleId(roleId);
			if(idList!=null && idList.size()>0){
				for (Integer resId : idList) {
					RolePermission rolePermissionResource=new RolePermission();
					rolePermissionResource.setId(0);
					rolePermissionResource.setPermissionId(resId);
					rolePermissionResource.setRoleId(roleId);
					rolePermissionResource.setControl(1);
					rolePermissionMapper.insert(rolePermissionResource);
				} 
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
