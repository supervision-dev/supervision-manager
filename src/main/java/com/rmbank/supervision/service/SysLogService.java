package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.SystemLog;

public interface SysLogService {
  
	List<SystemLog> getLogList(SystemLog log);

	int getLogCount(SystemLog log); 

	void saveOrUpdateLog(SystemLog log); 
	
	void writeLog(int moudleId, String description,int operation,int userId,int orgId,String ip);

}
