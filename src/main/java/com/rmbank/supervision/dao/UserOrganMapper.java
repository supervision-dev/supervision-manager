package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.UserOrgan;
@MyBatisRepository
public interface UserOrganMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserOrgan record);

    int insertSelective(UserOrgan record);

    UserOrgan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrgan record);

    int updateByPrimaryKey(UserOrgan record);
    
    /**
     * 根据用户ID查询用户所属的机构
     * 
     */
    List<Organ> getOrgsByUserId(Integer userId);

    /**
     * 根据用户ID删除对应的记录
     * @param id
     * @return
     */
	boolean deleteByUserId(Integer userId);

	/**
     * 根据用户ID查询用户所属的职务
     * 
     */
	List<UserOrgan> getPostsByUserId(Integer userId);

	/**
	 * 根据用户ID查询所属的机构ID
	 * @param id
	 * @return
	 */
	List<Integer> getUserOrgIdsByUserId(Integer id);

	
}