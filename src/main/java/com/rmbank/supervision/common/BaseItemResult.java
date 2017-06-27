package com.rmbank.supervision.common;

import java.util.List;

import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;

public class BaseItemResult {
	public List<ItemProcess> ResultItemProcess ;
	public Item ResultItem;
	
	public List<ItemProcess> getResultItemProcess() {
		return ResultItemProcess;
	}
	public void setResultItemProcess(List<ItemProcess> resultItemProcess) {
		ResultItemProcess = resultItemProcess;
	}
	public Item getResultItem() {
		return ResultItem;
	}
	public void setResultItem(Item resultItem) {
		ResultItem = resultItem;
	}
	
}
