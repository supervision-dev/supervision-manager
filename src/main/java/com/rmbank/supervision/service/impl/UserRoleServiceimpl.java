package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.UserRoleMapper;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.UserRole;
import com.rmbank.supervision.service.UserRoleService;
@Service("userRoleService")
public class UserRoleServiceimpl implements UserRoleService{

	@Resource
	private UserRoleMapper userRoleMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(UserRole record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(UserRole record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserRole selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(UserRole record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(UserRole record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Role> getRolesByUserId(Integer id) {
		// TODO Auto-generated method stub
		return userRoleMapper.getRolesByUserId(id);
	}

	@Override
	public void deleteByUserId(Integer userId) {
		// TODO Auto-generated method stub
		
	}

}
