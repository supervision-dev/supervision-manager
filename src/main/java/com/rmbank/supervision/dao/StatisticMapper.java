package com.rmbank.supervision.dao;

import java.util.List;

import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.StatisticModel;

@MyBatisRepository
public interface StatisticMapper {

	StatisticModel loadTotalCount(Item item);
    
	List<StatisticModel> loadTotalStatistisList(Item item); 

	List<StatisticModel> loadBranchTotalStatistisList(Item item); 

	List<StatisticModel> loadSupportTotalStatistisList(Item item); 
}
