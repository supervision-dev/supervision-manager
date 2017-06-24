package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.ResourceConfigMapper;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.service.ResourceService;
@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {

	@Resource
	private ResourceConfigMapper resourceConfigMapper;
	
	
	@Override
	public List<ResourceConfig> getAllResourceByUserId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 获取资源集合
	 */
	@Override
	public List<ResourceConfig> getResourceList(ResourceConfig resourceConfig) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getResourceList(resourceConfig);
	}

	/**
	 * 获取符合条件的资源条数
	 */
	@Override
	public int getResourceCount(ResourceConfig resourceConfig) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getResourceCount(resourceConfig);
	}

	/**
	 * 编辑资源的时候根据ID查询资源回显页面
	 */
	@Override
	public ResourceConfig getResourceById(Integer id) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getResourceById(id);
	}

	/**
	 * 新增资源和修改资源
	 */
	@Override
	public boolean saveOrUpdateResource(ResourceConfig resourceConfig) {
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(resourceConfig.getId()>0){
				resourceConfigMapper.updateByPrimaryKeySelective(resourceConfig);
				isSuccess = true;
			}else{	               
				resourceConfigMapper.insertSelective(resourceConfig); 
				isSuccess = true;
         
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
		
	}

	/**
	 * 新增资源时检查资源是否已经存在
	 */
	@Override
	public List<ResourceConfig> getExistRresource(ResourceConfig resourceConfig) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getExistRresource(resourceConfig);
	}

	/**
	 * 根据ID删除资源
	 */
	@Override
	public boolean deleteResourceById(Integer id) {
		boolean isSuccess = false;
		try{
			resourceConfigMapper.deleteByPrimaryKey(id);
			isSuccess=true;			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<ResourceConfig> getResourceListBymoudleId(
			ResourceConfig resourceConfig) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getResourceListBymoudleId(resourceConfig);
	}

	@Override
	public List<ResourceConfig> getResourceConfigsByUserRoles(List<Role> roleList) {
		// TODO Auto-generated method stub
		return resourceConfigMapper.getResourceConfigsByUserRoles(roleList);
	}

}
