package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.Role;

public interface FunctionService {
	List<FunctionMenu> getFunctionMenuByParentId(Integer id);

	List<FunctionMenu> getFunctionMenusByUserRoles(List<Role> roleList);

	List<FunctionMenu> getFunctionMenuByParentId(FunctionMenu functionMenu);

	List<FunctionMenu> getFunctionMenuList(FunctionMenu functionMenu);

	FunctionMenu getFunctionMenusById(Integer moudleId);

	List<FunctionMenu> getFunctionByUserId(Integer id);
 
}
