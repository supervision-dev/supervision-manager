package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.ItemProcess;
@MyBatisRepository
public interface ItemProcessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemProcess record);

    int insertSelective(ItemProcess record);

    ItemProcess selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemProcess record);

    int updateByPrimaryKey(ItemProcess record);

	List<Integer> getItemProcessIdByUuid(String uuid);

	List<ItemProcess> getItemProcessItemId(Integer id);
}