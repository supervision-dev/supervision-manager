package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.ItemProcess;

public interface ItemProcessService {
	int deleteByPrimaryKey(Integer id);

    int insert(ItemProcess record);

    int insertSelective(ItemProcess record);

    ItemProcess selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemProcess record);

    int updateByPrimaryKey(ItemProcess record);

	List<Integer> getItemProcessIdByUuid(String uuid);

	List<ItemProcess> getItemProcessItemId(Integer id);

	boolean insertItemProcessByItemId(Integer itemId);
}
