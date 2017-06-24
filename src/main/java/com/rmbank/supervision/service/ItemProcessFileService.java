package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.ItemProcessFile;

public interface ItemProcessFileService {
	int deleteByPrimaryKey(Integer id);

    int insert(ItemProcessFile record);

    int insertSelective(ItemProcessFile record);

    ItemProcessFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemProcessFile record);

    int updateByPrimaryKey(ItemProcessFile record);

	boolean insertSelective(ItemProcessFile ipf, List<Integer> ipid);

	List<ItemProcessFile> getFileListByItemId(Integer id);
}
