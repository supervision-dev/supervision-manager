package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.MetaMapper;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.service.ConfigService;


@Service("configService")
public class ConfigServiceimpl implements ConfigService {

	@Resource 
	private MetaMapper metaMapper;
	
	@Override
	public List<Meta> getConfigByUserId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public Meta getConfigById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Meta> getExistConfig(Meta meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveOrUpdateConfig(Meta meta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteConfigById(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 获取配置集合
	 */
	@Override
	public List<Meta> getConfigList(Meta meta) {
		// TODO Auto-generated method stub
		return metaMapper.getMetaList(meta);
	}

	/**
	 * 获取配置的记录总数
	 */
	@Override
	public int getConfigCount(Meta meta) {
		// TODO Auto-generated method stub
		return metaMapper.getConfigCount(meta);
	}

	/**
	 * 根据主键获取配置
	 */
	@Override
	public Meta selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return metaMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据关键字获取职务
	 */
	@Override
	public List<Meta> getMeatListByKey(String key) {
		// TODO Auto-generated method stub
		return metaMapper.getMeatListByKey(key);
	}

	/**
	 * 修改配置状态
	 */
	@Override
	public boolean MetaStateById(Meta meta) {
		// TODO Auto-generated method stub		
		boolean isSuccess = false;
		try{
			metaMapper.MetaStateById(meta);	
			isSuccess =true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 获取pid为0的 
	 */
	@Override
	public List<Meta> getConfigListByPid(Meta meta) {
		// TODO Auto-generated method stub
		return metaMapper.getConfigListByPid(meta);
	}
	
	/**
	 * 新增/编辑配置
	 */
	@Override
	public boolean saveOrUpdateMeta(Meta meta) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(meta.getId()>0){
				metaMapper.updateByPrimaryKeySelective(meta);
				isSuccess = true;
			}else{			
				metaMapper.insert(meta); 
				isSuccess = true;	            
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 新增配置时检查改配置是否已经存在
	 */
	@Override
	public List<Meta> getExistMeta(Meta mt) {
		// TODO Auto-generated method stub
		return metaMapper.getExistMeta(mt);
	}

	/**
	 * 获取用户对应的职务列表
	 */
	@Override
	public List<Meta> getUserPost() {
		// TODO Auto-generated method stub
		return metaMapper.getUserPost();
	}

	/**
	 * 根据id删除配置
	 */
	@Override
	public boolean deleteMetaById(Integer id) {
		boolean isSuccess = false;
		try{
			metaMapper.deleteByPrimaryKey(id);
			isSuccess = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

}
