package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Organ;
@MyBatisRepository
public interface OrganMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Organ record);

    int insertSelective(Organ record);

    Organ selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Organ record);

    int updateByPrimaryKey(Organ record);
    
    List<Organ> getOrganList(Organ organ);
    
    List<Organ> getOrganListByPid(Organ organ);
    
    int getOrganCount(Organ organ);

	List<Organ> getOrganByPId(Organ organ);

	List<Organ> getExistOrgan(Organ o);

	List<Integer> getUserOrgIdsByList(List<Integer> userOrgIds);

	List<Organ> getUserOrgByList(List<Integer> userOrgIds);

	List<Organ> getOrganByOrgIds(List<Integer> userOrgIds);

	List<Organ> getUserOrgByUserId(Integer id);

}