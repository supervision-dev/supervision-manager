
package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Item;

public interface ItemService {
	
	int deleteByPrimaryKey(Integer id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);

	List<Item> getExistItem(Item im);

	boolean saveOrUpdateItem(Item item, Integer[] orgIds, String content);

	List<Item> getItemList(Item item);

	int getItemCount(Item item);

	boolean deleteItemById(Integer id);

	/**
	 * 实时监察类型的项目
	 * @param item
	 * @return
	 */
	List<Item> getItemListByType(Item item);

	List<Item> getItemListByLgOrg(Item item);
	
	/**
	 * 查询所有中支立项中支完成的项目
	 * @param item
	 * @return
	 */
	List<Item> getItemListByOrgType(Item item);

	/**
	 * 查询当前登录中支立的项目
	 * @param item
	 * @return
	 */
	List<Item> getItemListByOrgTypeAndLogOrg(Item item);

	/**
	 * 分行立项分行完成
	 * @param item
	 * @return
	 */
	List<Item> getItemListByFHLXFHWC(Item item);

	/**
	 * 分行立项中支完成
	 * @param item
	 * @return
	 */
	List<Item> getItemListByFHLXZZWC(Item item);
	
	//实时监察模块的分页
	int getItemCountBySSJC(Item item);

	//查询所有中支立项的条数
	int getItemCountZZLXALL(Item item);
	
	/**
	 * 查询当前登录机构的中支立项记录数
	 * @param item
	 * @return
	 */
	int getItemCountZZLX(Item item);

	/**
	 * 获取当前登录用户的项目
	 * @param item
	 * @return
	 */
	List<Item> getItemListByTypeAndLogOrg(Item item); 
	
	/**
	 * 实时监察模块当前用户的记录数
	 * @param item
	 * @return
	 */
	int getItemCountByLogOrgSSJC(Item item);

	/**
	 * 根据登录机构加载分行立项分行完成数据
	 * @param item
	 * @return
	 */
	List<Item> getItemListByLogOrgFHLXFHWC(Item item);
	
	/**
	 * 所有分行立项分行完成的记录数
	 * @param item
	 * @return
	 */
	int getItemCountByFHLXFHWC(Item item);

	/**
	 * 根据登录机构查询分行立项分行完成的记录
	 * @param item
	 * @return
	 */
	int getItemCountByLogOrgFHLXFHWC(Item item);

	/**
	 * 根据当前登录用户查询分行立项中支完成
	 * @param item
	 * @return
	 */
	List<Item> getItemListByLogOrgFHLXZZWC(Item item);

	/**
	 * 获取所有分行立项中支完成的记录数
	 * @param item
	 * @return
	 */
	int getItemCountByFHLXZZWCALL(Item item);

	/**
	 * 根据当前登录机构获取分行立项中支完成的记录数
	 * @param item
	 * @return
	 */
	int getItemCountByLogOrgFHLXZZWC(Item item);

	/**
	 * 执法监察进行到立项
	 * @param item
	 * @param content
	 * @param orgIds
	 * @return
	 */
	boolean updateByItemSelective(Item item, String content, Integer[] orgIds);

	/**
	 * 获取所有的实时监察模块未完成的项目，以判断是否预期
	 * 
	 * 添加的工作事项除外
	 * @return
	 */
	List<Item> getAllSSItemNotComplete();

	/**
	 * 获取所有的综合管理模块未完成的项目，以判断是否预期
	 * 
	 * 添加的工作事项除外
	 * @return
	 */
	List<Item> getAllZHItemNotComplete();

	/**
	 * 待办事项
	 * @param item
	 * @return
	 */
	

	List<Item> getItemListXNJCToList(Item item);

	int getItemCountBySSJCDB(Item item);

	List<Item> getItemListToListByLogOrg(Item item);

	int getItemCountToListByLogOrg(Item item); 
} 