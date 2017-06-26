package com.rmbank.supervision.common;
import java.util.List;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.StatisticModel;

public class StatisticModelList {
	private List<StatisticModel> totalList; 
	private StatisticModel statisticModel;
	private StatisticModel subStatisticModel;
	public List<StatisticModel> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<StatisticModel> totalList) {
		this.totalList = totalList;
	}
	public StatisticModel getStatisticModel() {
		return statisticModel;
	}
	public void setStatisticModel(StatisticModel statisticModel) {
		this.statisticModel = statisticModel;
	}
	public StatisticModel getSubStatisticModel() {
		return subStatisticModel;
	}
	public void setSubStatisticModel(StatisticModel subStatisticModel) {
		this.subStatisticModel = subStatisticModel;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	private Item item;
}
