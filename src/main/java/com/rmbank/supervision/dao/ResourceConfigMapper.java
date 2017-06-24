package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
@MyBatisRepository
public interface ResourceConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceConfig record);

    int insertSelective(ResourceConfig record);

    ResourceConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceConfig record);

    int updateByPrimaryKey(ResourceConfig record);

	List<ResourceConfig> getResourceList(ResourceConfig resourceConfig);

	int getResourceCount(ResourceConfig resourceConfig);

	ResourceConfig getResourceById(Integer id);

	List<ResourceConfig> getExistRresource(ResourceConfig resourceConfig);

	List<ResourceConfig> getResourceListBymoudleId(ResourceConfig resourceConfig);
	
	List<ResourceConfig> getResourceConfigsByUserRoles(List<Role> roleList);
}