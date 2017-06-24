
package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Item;
@MyBatisRepository
public interface ItemMapper {
    int deleteByPrimaryKey(Integer id);
    
    int updateItemStatus(Integer id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);

	List<Item> getExistItem(Item item);

	List<Item> getItemList(Item item);

	int getItemCount(Item item);

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

	/**
	 * 实时监察模块的分页
	 * @param item
	 * @return
	 */
	int getItemCountBySSJC(Item item);
	
	/**
	 * 实时监察模块当前用户的分页
	 * @param item
	 * @return
	 */
	int getItemCountByLogOrgSSJC(Item item);

	/**
	 * 查询所有中支立项的条数
	 * @param item
	 * @return
	 */
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
	 * 根据登录机构加载分行立项分行完成数据
	 * @param item
	 * @return
	 */
	List<Item> getItemListByLogOrgFHLXFHWC(Item item);

	/**
	 * 所有分行立项分行完成的项目
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
	 * 根据登录机构查询分行立项中支完成的记录
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
	 * 加载所有的实时监察模块未完成的记录
	 * @return
	 */
	List<Item> getAllSSItemNotComplete();

	/**
	 * 加载所有的综合模块未完成的记录
	 * @return
	 */
	List<Item> getAllZHItemNotComplete();

	
	List<Item> getItemListXNJCToList(Item item);

	int getItemCountBySSJCDB(Item item);

	List<Item> getItemListToListByLogOrg(Item item);

	int getItemCountToListByLogOrg(Item item); 
	
}