package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.GradeSchemeDetailMapper;
import com.rmbank.supervision.model.GradeSchemeDetail;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.service.GradeSchemeDetailService;

@Service("gradeSchemeDetailService")
public class GradeSchemeDetailServiceimpl implements GradeSchemeDetailService {

	@Resource
	private GradeSchemeDetailMapper gradeSchemeDetailMapper;
	
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(GradeSchemeDetail record) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.insert(record);
	}

	@Override
	public int insertSelective(GradeSchemeDetail record) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.insertSelective(record);
	}

	@Override
	public GradeSchemeDetail selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(GradeSchemeDetail record) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GradeSchemeDetail record) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GradeSchemeDetail> getGradeSchemeDetailListByPid(
			GradeSchemeDetail gradeSchemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailListByPid(gradeSchemeDetail);
	}
	@Override
	public List<GradeSchemeDetail> getGradeSchemeDetailListByGradeId(
			GradeSchemeDetail gradeSchemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailListByGradeId(gradeSchemeDetail);
	}

	@Override
	public int getGradeSchemeDetailCount(GradeSchemeDetail gradeSchemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailCount(gradeSchemeDetail);
	}

	@Override
	public List<GradeSchemeDetail> getSchemeDetailListByPidAndGradeId(
			GradeSchemeDetail gradeSchemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getSchemeDetailListByPidAndGradeId(gradeSchemeDetail);
	}

	@Override
	public List<GradeSchemeDetail> getGradeSchemeDetailList(
			GradeSchemeDetail schemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailList(schemeDetail);
	}

	@Override
	public List<GradeSchemeDetail> getGradeSchemeDetailTreeByPid(
			GradeSchemeDetail schemeDetail) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailTreeByPid(schemeDetail);
	}

	@Override
	public boolean saveOrUpdateDetail(GradeSchemeDetail detail) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(detail.getId()>0){
				gradeSchemeDetailMapper.updateByPrimaryKeySelective(detail);
				isSuccess = true;
			}else{	               
				gradeSchemeDetailMapper.insertSelective(detail); 
				isSuccess = true;
         
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<GradeSchemeDetail> getExistDetail(GradeSchemeDetail gsd) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getExistDetail(gsd);
	}

	@Override
	public GradeSchemeDetail getGradeSchemeDetailById(Integer id) {
		// TODO Auto-generated method stub
		return gradeSchemeDetailMapper.getGradeSchemeDetailById(id);
	}

	

}
