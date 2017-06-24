package com.rmbank.supervision.service;

import java.util.List;

import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.StatisticModel;

public interface StatisticService {
	
	/**
	 * 获取实时监察模块，所有记录总数
	 * @param item
	 * @return
	 */
	StatisticModel loadTotalCount(Item item);
	
	/**
	 * 获取实时监察各类监察所有已经立项的记录数
	 * @param item
	 * @return
	 */
	List<StatisticModel> loadTotalStatistisList(Item item); 

	/**
	 * 获取分行立项 的所有记录数
	 * @param item
	 * @return
	 */
	List<StatisticModel> loadBranchTotalStatistisList(Item item); 
	
	 
	/**
	 * 获取中支立项 的所有记录数
	 * @param item
	 * @return
	 */
	List<StatisticModel> loadSupportTotalStatistisList(Item item);

}
