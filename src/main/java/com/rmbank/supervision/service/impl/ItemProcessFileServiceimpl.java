package com.rmbank.supervision.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.ItemProcessFileMapper;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.ItemProcessFile;
import com.rmbank.supervision.service.ItemProcessFileService;

@Service("itemProcessFileService")
public class ItemProcessFileServiceimpl implements ItemProcessFileService {

	@Resource
	private ItemProcessFileMapper itemProcessFileMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(ItemProcessFile record) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.insert(record);
	}

	@Override
	public int insertSelective(ItemProcessFile record) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.insertSelective(record);
	}

	@Override
	public ItemProcessFile selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(ItemProcessFile record) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ItemProcessFile record) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.updateByPrimaryKey(record);
	}

	@Override
	public boolean insertSelective(ItemProcessFile ipf, List<Integer> ipid) {
		boolean isSuccess = false;
		try{
			for (Integer id : ipid) {
				ipf.setItemProcessId(id);
				itemProcessFileMapper.insertSelective(ipf);
			}	
			isSuccess = true;
         
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<ItemProcessFile> getFileListByItemId(Integer id) {
		// TODO Auto-generated method stub
		return itemProcessFileMapper.getFileListByItemId(id);
	}

}
