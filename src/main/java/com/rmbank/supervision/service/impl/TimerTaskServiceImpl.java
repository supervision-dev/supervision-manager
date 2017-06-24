package com.rmbank.supervision.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.DateUtil;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.TimeerTaskService;
@Component
public class TimerTaskServiceImpl implements  TimeerTaskService{


	@Resource
	private ItemService itemService;
	@Resource
	private SysLogService logService;
 
//	@Scheduled(cron="0 0 9 * * ?")
	@Override 
	@Scheduled(fixedDelay = 7200000) 
	public void itemSSOverTimeTask() {
		// TODO Auto-generated method stub
		List<Item> itemList = itemService.getAllSSItemNotComplete();
		Date nowDate = new Date();
		for(Item item : itemList){
			if(item.getStatus() != Constants.ITEM_STATUS_NEW && item.getStatus() != Constants.ITEM_STATUS_OVER_COMPLETE 
					&& item.getStatus() != Constants.ITEM_STATUS_OVER && item.getEndTime() != null){
				if(!item.getEndTime().after(nowDate)){
					int betDay = DateUtil.getBetweenDays(nowDate, item.getEndTime());
					item.setStatus(Constants.ITEM_STATUS_OVERLINE);
					String modulename =  item.getSupervisionTypeId()==2?"效能监察":item.getSupervisionTypeId()==3?"廉政监察":"执法监察";
					itemService.updateByPrimaryKeySelective(item);
					logService.writeLog(Constants.LOG_TYPE_SYS, modulename+"项目"+item.getName() +"已经逾期"+betDay+"天，请及时处理完成", 4, 1, 15, "127.0.0.1");
				}
			}
		}
	}
	 
	@Override
	@Scheduled(fixedDelay = 7200000) 
	public void itemZHOverTimeTask() {
		// TODO Auto-generated method stub
		List<Item> itemList = itemService.getAllSSItemNotComplete();
		Date nowDate = new Date();
		for(Item item : itemList){
			if(item.getStatus() != Constants.ITEM_STATUS_NEW && item.getStatus() != Constants.ITEM_STATUS_OVER_COMPLETE 
					&& item.getStatus() != Constants.ITEM_STATUS_OVER && item.getEndTime() != null){
				if(!item.getEndTime().after(nowDate)){
					int betDay = DateUtil.getBetweenDays(nowDate, item.getEndTime());
					item.setStatus(Constants.ITEM_STATUS_OVERLINE);
					itemService.updateByPrimaryKeySelective(item);
					logService.writeLog(Constants.LOG_TYPE_SYS, "综合管理项目"+item.getName() +"已经逾期"+betDay+"天，请及时处理完成", 4, 1, 15, "127.0.0.1");
				}
			}
		}
	}


}
