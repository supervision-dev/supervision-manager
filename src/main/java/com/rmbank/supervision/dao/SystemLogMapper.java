package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.SystemLog;
@MyBatisRepository
public interface SystemLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemLog record);

    int insertSelective(SystemLog record);

    SystemLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemLog record);

    int updateByPrimaryKey(SystemLog record);

	List<SystemLog> getLogList(SystemLog log);

	int getLogCount(SystemLog log);
}