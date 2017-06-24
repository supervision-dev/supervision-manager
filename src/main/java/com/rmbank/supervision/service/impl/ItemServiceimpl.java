 
package com.rmbank.supervision.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.dao.ItemMapper;
import com.rmbank.supervision.dao.ItemProcessMapper;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.service.ItemService;
@Service(value="itemService")
public class ItemServiceimpl implements ItemService {
	@Resource 
	private ItemMapper itemMapper;
	@Resource
	private ItemProcessMapper itemProcessMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(Item record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(Item record) {
		// TODO Auto-generated method stub
		return itemMapper.insertSelective(record);
	}

	@Override
	public Item selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return itemMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Item record) {
		// TODO Auto-generated method stub
		return itemMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Item record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Item> getExistItem(Item im) {
		// TODO Auto-generated method stub
		return itemMapper.getExistItem(im);
	}

	@Override
	public boolean saveOrUpdateItem(Item item, Integer[] orgIds,String content) {
		boolean isSuccess = false;
		int contentType = Constants.CONTENT_TYPE_ID_1;
		if(item.getItemType() == Constants.STATIC_ITEM_TYPE_SVISION)
		{
			if(item.getSupervisionTypeId() == Constants.SUPERVISION_TYPE_ID_XL){
				contentType = 66;
			}else if(item.getSupervisionTypeId() == Constants.SUPERVISION_TYPE_ID_LZ){
				contentType = 72;
			}else if(item.getSupervisionTypeId() == Constants.SUPERVISION_TYPE_ID_ZF){
				contentType = 79;
			}
		}
		try{	
			//新增项目成功后返回的itemId的集合 
			List<Integer> itemId=new ArrayList<Integer>(); 			
			for (Integer i : orgIds) {				
				item.setSupervisionOrgId(i);
				itemMapper.insertSelective(item); 
				itemId.add(item.getId());
				item.setStatus(0);
				item.setId(Constants.ITEM_STATUS_NEW);
			}
			ItemProcess itemProcess=new ItemProcess();	
			int i = 0;
			for (Integer integer : itemId) {
				itemProcess.setUuid(item.getUuid());
				itemProcess.setItemId(integer);
				//itemProcess.setDefined();
				itemProcess.setContent(content);
				itemProcess.setOrgId(orgIds[i]);
				i ++;
				if(item.getItemType()==Constants.STATIC_ITEM_TYPE_MANAGE){
					itemProcess.setContentTypeId(contentType);
				}else {
					itemProcess.setContentTypeId(-1); 
				}				
				itemProcess.setPreparerTime(new Date());
				itemProcess.setPreparerId(item.getPreparerId());
				itemProcess.setPreparerOrgId(item.getPreparerOrgId());
				itemProcessMapper.insertSelective(itemProcess);
			}
			
			isSuccess = true;
         
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<Item> getItemList(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemList(item);
	}

	@Override
	public int getItemCount(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCount(item);
	}

	@Override
	public boolean deleteItemById(Integer id) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try{
			//itemMapper.deleteByPrimaryKey(id);
			itemMapper.updateItemStatus(id);
			isSuccess = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<Item> getItemListByType(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByType(item);
	}

	@Override
	public List<Item> getItemListByLgOrg(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByLgOrg(item);
	}

	/**
	 * 查询所有中支立项中支完成的项目
	 * @param item
	 * @return
	 */
	@Override
	public List<Item> getItemListByOrgType(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByOrgType(item);
	}

	/**
	 * 查询当前登录中支立的项目
	 * @param item
	 * @return
	 */
	@Override
	public List<Item> getItemListByOrgTypeAndLogOrg(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByOrgTypeAndLogOrg(item);
	}

	/**
	 * 分行立项分行完成
	 */
	@Override
	public List<Item> getItemListByFHLXFHWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByFHLXFHWC(item);
	}

	/**
	 * 分行立项中支完成
	 */
	@Override
	public List<Item> getItemListByFHLXZZWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByFHLXZZWC(item);
	}

	//实时监察模块的分页
	@Override
	public int getItemCountBySSJC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountBySSJC(item);
	}

	//所有中支立项的条数
	@Override
	public int getItemCountZZLXALL(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountZZLXALL(item);
	}

	@Override
	public int getItemCountZZLX(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountZZLX(item);
	}

	@Override
	public List<Item> getItemListByTypeAndLogOrg(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByTypeAndLogOrg(item);
	}

	@Override
	public int getItemCountByLogOrgSSJC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountByLogOrgSSJC(item);
	}

	@Override
	public List<Item> getItemListByLogOrgFHLXFHWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByLogOrgFHLXFHWC(item);
	}

	@Override
	public int getItemCountByFHLXFHWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountByFHLXFHWC(item);
	}

	/**
	 * 根据登录机构查询分行立项分行完成的记录
	 * @param item
	 * @return
	 */
	@Override
	public int getItemCountByLogOrgFHLXFHWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountByLogOrgFHLXFHWC(item);
	}
	/**
	 * 根据登录机构查询分行立项中支完成的记录
	 * @param item
	 * @return
	 */
	@Override
	public List<Item> getItemListByLogOrgFHLXZZWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListByLogOrgFHLXZZWC(item);
	}

	/**
	 * 获取所有分行立项中支完成的记录数
	 * @param item
	 * @return
	 */
	@Override
	public int getItemCountByFHLXZZWCALL(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountByFHLXZZWCALL(item);
	}

	/**
	 * 根据当前登录机构获取分行立项中支完成的记录数
	 * @param item
	 * @return
	 */
	@Override
	public int getItemCountByLogOrgFHLXZZWC(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountByLogOrgFHLXZZWC(item);
	}

	/**
	 * 执法监察立项
	 */
	@Override
	public boolean updateByItemSelective(Item item, String content, Integer[] orgIds) {
		boolean isSuccess = false;		
		try{
			ItemProcess itemProcess=new ItemProcess();
			if(item.getSuperItemType() == 61){//综合执法
				item.setSupervisionOrgId(orgIds[0]);
				itemMapper.updateByPrimaryKeySelective(item);
				
				itemProcess.setUuid(item.getUuid());
				itemProcess.setItemId(item.getId());
				itemProcess.setContent(content);
				itemProcess.setOrgId(orgIds[0]);
				itemProcess.setContentTypeId(-1);
				itemProcess.setPreparerTime(new Date());
				itemProcess.setPreparerId(item.getPreparerId());
				itemProcess.setPreparerOrgId(item.getPreparerOrgId());
				itemProcessMapper.insertSelective(itemProcess);
			}
//			//新增项目成功后返回的itemId的集合 
//			List<Integer> itemId=new ArrayList<Integer>(); 			
//			for (Integer i : orgIds) {				
//				item.setSupervisionOrgId(i);
//				itemMapper.insertSelective(item); 
//				itemId.add(item.getId());
//				item.setStatus(0);
//				item.setId(Constants.ITEM_STATUS_NEW);
//			}
				
			/*int i = 0;
			for (Integer integer : itemId) {
				itemProcess.setUuid(item.getUuid());
				itemProcess.setItemId(integer);
				//itemProcess.setDefined();
				itemProcess.setContent(content);
				itemProcess.setOrgId(orgIds[i]);
				i ++;
				if(item.getItemType()==Constants.STATIC_ITEM_TYPE_MANAGE){
					itemProcess.setContentTypeId(contentType);
				}else {
					itemProcess.setContentTypeId(-1); 
				}				
				itemProcess.setPreparerTime(new Date());
				itemProcess.setPreparerId(item.getPreparerId());
				itemProcess.setPreparerOrgId(item.getPreparerOrgId());
				itemProcessMapper.insertSelective(itemProcess);
			}*/
			
			isSuccess = true;
         
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<Item> getAllSSItemNotComplete() {
		// TODO Auto-generated method stub
		return itemMapper.getAllSSItemNotComplete();
	}

	@Override
	public List<Item> getAllZHItemNotComplete() {
		// TODO Auto-generated method stub
		return itemMapper.getAllZHItemNotComplete();
	}

	

	@Override
	public List<Item> getItemListXNJCToList(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListXNJCToList(item);
	}

	@Override
	public int getItemCountBySSJCDB(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountBySSJCDB(item);
	}

	@Override
	public List<Item> getItemListToListByLogOrg(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemListToListByLogOrg(item);
	}

	@Override
	public int getItemCountToListByLogOrg(Item item) {
		// TODO Auto-generated method stub
		return itemMapper.getItemCountToListByLogOrg(item);
	}

}  
