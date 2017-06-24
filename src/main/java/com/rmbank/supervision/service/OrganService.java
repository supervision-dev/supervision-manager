package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.UserOrgan;


public interface OrganService {
	int deleteByPrimaryKey(Integer id);

    int insert(Organ record);

    int insertSelective(Organ record);

    Organ selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Organ record);
    
    List<Organ> getOrganByPId(Organ organ);    

    int updateByPrimaryKey(Organ record);
    
    List<Organ> getOrganList(Organ organ);
    
    List<Organ> getOrganListByPid(Organ organ);

	int getOrganCount(Organ organ);

	

	/**
	 * 根据用户ID去查询用户用户所属的机构
	 * @param id
	 * @return
	 */
	List<Organ> getOrgsByUserId(Integer id);

	/**
	 * 删除机构
	 */
	boolean deleteOrgByid(Integer id);
	
	/**
	 * 修改机构状态
	 */
	boolean updateOrgStateByid(Organ organ);

	/**
	 * 新增编辑机构
	 * @param organ
	 * @return
	 */
	boolean saveOrUpdateOrgan(Organ organ);

	/**
	 * 编辑机构时根据pid查询机构
	 * @param organ
	 * @return
	 */
	//List<Organ> getOrganByPid(Organ organ);
	
	/**
	 * 新增机构的时候判断机构名称是否存在
	 * @param o
	 * @return
	 */
	List<Organ> getExistOrgan(Organ o);

	/**
	 * 编辑用户时回显用户对应的职务
	 * @param id
	 * @return
	 */
	List<UserOrgan> getPostsByUserId(Integer userId);

	/**
	 * 根据用户所属的机构ID加载树
	 * @param userOrgIds
	 * @return
	 */
	List<Organ> getOrganByOrgIds(List<Integer> userOrgIds);
    

}
