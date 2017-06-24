package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Role;


public interface RoleService {

	List<Role> getRolesByUserId(Integer id);

	
	List<Role> getRoleList(Role role);
	
	int getRoleCount(Role role);

	Role getRoleById(Integer id);

	List<Role> getExistRole(Role role);

	boolean saveOrUpdateRole(Role role);

	boolean deleteRoleById(Integer id);

}
