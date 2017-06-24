package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Meta;


public interface ConfigService {
	List<Meta> getConfigByUserId(Integer id);
	
	List<Meta> getConfigList(Meta meta);
	
	int getConfigCount(Meta meta);

	Meta getConfigById(Integer id);

	Meta selectByPrimaryKey(Integer id);
	
	List<Meta> getExistConfig(Meta meta);

	boolean saveOrUpdateConfig(Meta meta);

	boolean deleteConfigById(Integer id);

	List<Meta> getMeatListByKey(String key);

	boolean MetaStateById(Meta meta);
	
	List<Meta> getConfigListByPid(Meta meta);

	boolean saveOrUpdateMeta(Meta meta);

	List<Meta> getExistMeta(Meta mt);

	List<Meta> getUserPost();

	boolean deleteMetaById(Integer id);

	
}
