package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.OrganMapper;
import com.rmbank.supervision.dao.UserOrganMapper;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.UserOrgan;
import com.rmbank.supervision.service.OrganService;

@Service("organService")
public class OrganServiceimpl implements OrganService {
	
	@Resource
	private OrganMapper organMapper;
	@Resource
	private UserOrganMapper userOrganMapper;
	
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(Organ record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(Organ record) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int updateByPrimaryKeySelective(Organ record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Organ record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Organ selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return organMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public List<Organ> getOrganList(Organ organ) {
		// TODO Auto-generated method stub
		return organMapper.getOrganList(organ);
	}

	@Override
	public List<Organ> getOrganListByPid(Organ organ) {
		// TODO Auto-generated method stub
		return organMapper.getOrganListByPid(organ);
	}

	
	@Override
	public int getOrganCount(Organ organ) {
		// TODO Auto-generated method stub
		return organMapper.getOrganCount(organ);
	}

	@Override
	public List<Organ> getOrganByPId(Organ organ) {
		// TODO Auto-generated method stub
		return organMapper.getOrganByPId(organ);
	}

	@Override
	public List<Organ> getOrgsByUserId(Integer id) {
		// TODO Auto-generated method stub
		return userOrganMapper.getOrgsByUserId(id);
	}

	@Override
	public boolean deleteOrgByid(Integer id) {
		boolean isSuccess = false;
		try{
			organMapper.deleteByPrimaryKey(id);
			isSuccess = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public boolean updateOrgStateByid(Organ organ) {
		boolean isSuccess = false;
		try{
			organMapper.updateByPrimaryKeySelective(organ);
			isSuccess = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public boolean saveOrUpdateOrgan(Organ organ) {
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(organ.getId()>0){
				organMapper.updateByPrimaryKeySelective(organ);
				isSuccess = true;
			}else{	               
				organMapper.insert(organ); 
				isSuccess = true;
         
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<Organ> getExistOrgan(Organ o) {
		// TODO Auto-generated method stub
		return organMapper.getExistOrgan(o);
	}

	@Override
	public List<UserOrgan> getPostsByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return userOrganMapper.getPostsByUserId(userId);
	}

	@Override
	public List<Organ> getOrganByOrgIds(List<Integer> userOrgIds) {
		// TODO Auto-generated method stub
		return organMapper.getOrganByOrgIds(userOrgIds);
	}

	@Override
	public Organ getOrganByPidAndName(Integer pid, String orgName) {
		// TODO Auto-generated method stub
		return organMapper.getOrganByPidAndName(pid,orgName);
	}

	
	/*
	 * 自己@Override
	public List<Organ> getOrganByPid(Organ organ) {
		// TODO Auto-generated method stub
		return organMapper.getOrganByPId(organ);
	}*/

	
}
