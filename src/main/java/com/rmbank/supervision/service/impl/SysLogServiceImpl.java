package com.rmbank.supervision.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.dao.SystemLogMapper;
import com.rmbank.supervision.model.SystemLog;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.SysLogService;

@Service("logService")
public class SysLogServiceImpl implements SysLogService {

	@Resource
	private SystemLogMapper logMapper; 
	
	@Override
	public List<SystemLog> getLogList(SystemLog log) 
	{
		// TODO Auto-generated method stub
		return logMapper.getLogList(log);
	}

	@Override
	public int getLogCount(SystemLog log) 
	{
		// TODO Auto-generated method stub
		return logMapper.getLogCount(log);
	}
	
	@Override
	public void saveOrUpdateLog(SystemLog log) {
		// TODO Auto-generated method stub 
		logMapper.insertSelective(log); 
	}
	@Override
	public void writeLog(int moudleId, String description,int operation,int userId,int orgId,String ip) {
		// TODO Auto-generated method stub
		SystemLog log = new SystemLog();
		log.setMoudleId(moudleId);
		log.setTableName("");
		log.setOperation(operation);
		log.setDescription(description);
		log.setOperTime(new Date());
		log.setOperId(userId);
		log.setOrgId(orgId);
		log.setIp(ip);
		logMapper.insertSelective(log);		
	}
 
}
