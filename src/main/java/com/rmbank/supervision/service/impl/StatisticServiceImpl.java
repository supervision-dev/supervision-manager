package com.rmbank.supervision.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.dao.StatisticMapper;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.StatisticModel;
import com.rmbank.supervision.service.StatisticService;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {
	
	@Resource 
	private StatisticMapper statisticMapper;

	@Override
	public StatisticModel loadTotalCount(Item item) {
		// TODO Auto-generated method stub
		return statisticMapper.loadTotalCount(item);
	} 
	
	@Override
	public List<StatisticModel> loadTotalStatistisList(Item item) {
		// TODO Auto-generated method stub
		return statisticMapper.loadTotalStatistisList(item);
	} 

	@Override
	public List<StatisticModel> loadBranchTotalStatistisList(Item item) {
		// TODO Auto-generated method stub
		return statisticMapper.loadBranchTotalStatistisList(item);
	} 

	@Override
	public List<StatisticModel> loadSupportTotalStatistisList(Item item) {
		// TODO Auto-generated method stub
		return statisticMapper.loadSupportTotalStatistisList(item);
	} 
	
	
}
