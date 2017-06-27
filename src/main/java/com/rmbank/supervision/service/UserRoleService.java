package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.UserRole;

public interface UserRoleService {
	 int deleteByPrimaryKey(Integer id);

	    int insert(UserRole record);

	    int insertSelective(UserRole record);

	    UserRole selectByPrimaryKey(Integer id);

	    int updateByPrimaryKeySelective(UserRole record);

	    int updateByPrimaryKey(UserRole record);
	    
	    /**
	     * 根据用户ID去查询用户角色
	     * @param id
	     * @return
	     */
		List<Role> getRolesByUserId(Integer id);

		/**
		 * 根据用户id删除
		 * @param id
		 */
		void deleteByUserId(Integer userId);
}
