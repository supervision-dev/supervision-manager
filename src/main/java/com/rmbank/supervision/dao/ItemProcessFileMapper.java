package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.ItemProcessFile;
@MyBatisRepository
public interface ItemProcessFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemProcessFile record);

    int insertSelective(ItemProcessFile record);

    ItemProcessFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemProcessFile record);

    int updateByPrimaryKey(ItemProcessFile record); 	

	List<ItemProcessFile> getFileListByItemId(Integer id);
}