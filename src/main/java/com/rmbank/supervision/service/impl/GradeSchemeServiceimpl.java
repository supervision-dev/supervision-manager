package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.GradeSchemeMapper;
import com.rmbank.supervision.dao.ItemProcessGradeMapper;
import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.ItemProcessGrade;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.service.GradeSchemeService;

@Service("gradeSchemeService")
public class GradeSchemeServiceimpl implements GradeSchemeService {

	@Resource
	private GradeSchemeMapper gradeSchemeMapper;
	@Resource
	private ItemProcessGradeMapper itemProcessGradeMapper;
	
	@Override
	public List<GradeScheme> getGradeSchemeList(GradeScheme gradeScheme) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.getGradeSchemeList(gradeScheme);
	}

	@Override
	public int getGradeSchemeCount(GradeScheme gradeScheme) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.getGradeSchemeCount(gradeScheme);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(GradeScheme record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(GradeScheme record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GradeScheme selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(GradeScheme record) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GradeScheme record) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.updateByPrimaryKey(record);
	}

	@Override
	public boolean saveOrUpdateScheme(GradeScheme scheme) {
		boolean isSuccess = false;
		try{
			//id存在则为修改操作
			if(scheme.getId()>0){
				gradeSchemeMapper.updateByPrimaryKeySelective(scheme);
				isSuccess = true;
			}else{	               
				gradeSchemeMapper.insert(scheme); 
				isSuccess = true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<GradeScheme> getExistGradeScheme(GradeScheme sch) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.getExistGradeScheme(sch);
	}

	@Override
	public List<GradeScheme> getGradeSchemeListASC(GradeScheme gradeScheme) {
		// TODO Auto-generated method stub
		return gradeSchemeMapper.getGradeSchemeListASC(gradeScheme);
	}

	@Override
	public void insertGradeList(List<ItemProcessGrade> gradeList) {
		// TODO Auto-generated method stub
		itemProcessGradeMapper.insertGradeList(gradeList);
	}

	@Override
	public List<ItemProcessGrade> getGradeListByItemProcessId(Integer id) {
		// TODO Auto-generated method stub
		return itemProcessGradeMapper.getGradeListByItemProcessId(id);
	}

}
