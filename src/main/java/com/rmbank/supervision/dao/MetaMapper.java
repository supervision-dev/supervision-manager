package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Meta;



@MyBatisRepository
public interface MetaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Meta record);

    int insertSelective(Meta record);

    Meta selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Meta record);

    int updateByPrimaryKey(Meta record);

	List<Meta> getMetaList(Meta meta);

	int getConfigCount(Meta meta);
	
	Meta getConfigById(Integer id);

	List<Meta> getMeatListByKey(String key);

	void MetaStateById(Meta meta);

	List<Meta> getConfigListByPid(Meta meta);

	List<Meta> getExistMeta(Meta mt);

	List<Meta> getUserPost();
}