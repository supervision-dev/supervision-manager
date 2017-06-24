package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.User;
@MyBatisRepository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    void updateByPrimaryKey(User record);

	User getUserByAccount(User temp);
	
	/**
	 * 获取用户列表
	 * @return
	 */
	List<User> getUserList(User user);
	/**
	 * 获取用户条数
	 * 
	 * @param user
	 * @return
	 */
	int getUserCount(User user);
	
	/**
	 * 编辑用户回显用户信息
	 * @param id
	 * @return
	 */
	User getUserById(Integer id);

	/**
	 * 新增用户时判断用户是否存在
	 * @param u
	 * @return
	 */
	List<User> getExistUser(User u);

	/**
	 * 根据id修改用户状态
	 * @param user
	 */
	void updateUserUsedById(User user);

	/**
	 * 根据id删除用户
	 * @param id
	 */
	void deleteUserById(Integer id);

	List<User> getUserListByOrgId(User lgUser);

	int getUserCountByOrgId(User lgUser);

	List<User> getUserByOrgids(List<Integer> userOrgIds);

	List<User> getUserListByOrgId(Integer orgId);
	
	/**
	 * 获取当前登录用户的所属机构下的所有用户
	 * @param userOrgIds
	 * @return
	 */
	List<User> getUserListByLgUser(User lgUser);
}