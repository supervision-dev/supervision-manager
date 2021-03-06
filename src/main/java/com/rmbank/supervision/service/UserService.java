package com.rmbank.supervision.service;

import java.util.List;
import java.util.Map;

import com.rmbank.supervision.common.ReturnResult;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;



public interface UserService {

	ReturnResult<User> login(String name, String pwd, boolean rememberMe);

	User getUserByAccount(String username);
	
	List<User> getUserList(User user);
	
	int getUserCount(User user);

	boolean updateByPrimaryKey(User user);
	
	User getUserById(Integer id);

	List<User> getExistUser(User u);

	boolean saveOrUpdateUser(User user, List<Integer> roleIds , List<Integer> orgIds, Integer postId);

	boolean deleteUserById(Integer id);

	boolean updateUserUsedById(User user);

	List<User> getUserListByOrgId(User lgUser);

	int getUserCountByOrgId(User lgUser);

	List<Integer> getUserOrgIdsByUserId(Integer id);

	List<Integer> getUserOrgIdsByList(List<Integer> userOrgIds);

	List<User> getUserByOrgids(List<Integer> userOrgIds);
	
	List<Organ> getUserOrgByList(List<Integer> userOrgIds);

	List<User> getUserListByOrgId(Integer orgId);

	List<Organ> getUserOrgByUserId(Integer id);

	List<User> getUserListByLgUser(User lgUser);

	void updateByPrimaryKeySelective(User user);
	
	int updateByAccount(User user);
	
	List<User> getUserByisLocking(User user);

	void resetPwd(User user); 
	
	List<User> getUserAll();

	User getUserBySearchName(User user);

	List<User> getUserByOrgidsAndSearchUser(Map<String, Object> map);

	int getUserCountByOrgidsAndSearchUser(Map<String, Object> map);
}
