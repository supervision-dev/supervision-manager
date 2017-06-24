package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.FunctionMenuMapper;
import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.service.FunctionService;
@Service("functionService")
public class FunctionServiceImpl implements FunctionService{

    @Resource
    private FunctionMenuMapper functionMenuMapper;

	@Override
	public List<FunctionMenu> getFunctionMenuByParentId(Integer id) {
		FunctionMenu functionMenu = new FunctionMenu();
        functionMenu.setParentId(id);
        return functionMenuMapper.getFunctionMenuByCondition(functionMenu);
    }

	@Override
	public List<FunctionMenu> getFunctionMenusByUserRoles(List<Role> roleList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FunctionMenu> getFunctionMenuByParentId(FunctionMenu functionMenu) {
		// TODO Auto-generated method stub
		return functionMenuMapper.getFunctionMenuByParentId(functionMenu);
	}

	@Override
	public List<FunctionMenu> getFunctionMenuList(FunctionMenu functionMenu) {
		// TODO Auto-generated method stub
		return functionMenuMapper.getFunctionMenuList(functionMenu);
	}

	@Override
	public FunctionMenu getFunctionMenusById(Integer id) {
		// TODO Auto-generated method stub
		return functionMenuMapper.getFunctionMenusById(id);
	}

	@Override
	public List<FunctionMenu> getFunctionByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return functionMenuMapper.getFunctionByUserId(userId);
	}
}
