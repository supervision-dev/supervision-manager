package com.rmbank.supervision.web.controller.statistic;

import java.io.UnsupportedEncodingException; 
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
  






import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.Item; 
import com.rmbank.supervision.model.StatisticModel; 
import com.rmbank.supervision.service.StatisticService;

/**
 * 分行立项的Action
 * @author LQ
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/statistic")
public class StatisticAction {

	/**
	 * 资源注入
	 */
	@Resource
	private StatisticService statisticService;
		/**
		 * 效能监察统计
		 * @param request
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		@RequestMapping("/efficiency/efficencyStatistic.do")
		@RequiresPermissions("statistic/efficiency/efficencyStatistic.do")
		public String efficencyStatistic(Item item, 
				HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
			
			List<StatisticModel> totalList = new ArrayList<StatisticModel>();   
			StatisticModel statisticModel = new StatisticModel();
			StatisticModel subStatisticModel = new StatisticModel();

			int totalCount = 0;
			int comCount = 0;
			int unComCount = 0;
			int overUnComCount = 0;
			int overComCount = 0;
			if(StringUtil.isEmpty(item.getSchBeginTime())){
				item.setSchBeginTime(null);
			}else{
				item.setSchBeginTime(item.getSchBeginTime()+" 00:00:00");
			}
			if(StringUtil.isEmpty(item.getSchEndTime())){
				item.setSchEndTime(null);
			}else{
				item.setSchEndTime(item.getSchEndTime()+" 23:59:59");
			}
			 
			item.setSupervisionTypeId(Constants.SUPERVISION_TYPE_ID_XL);  
			 try{
				 statisticModel = statisticService.loadTotalCount(new Item());
				 totalList = statisticService.loadTotalStatistisList(item);    
				 for(StatisticModel sm : totalList){
					 totalCount = totalCount + sm.getTotalCount(); 
					 comCount = comCount + sm.getComCount();
					 unComCount = unComCount+ sm.getUnComCount();
					 overUnComCount = overUnComCount+ sm.getOverUnComCount();
					 overComCount = overComCount+ sm.getOverComCount();
				 }
				 subStatisticModel.setTotalCount(totalCount);
				 subStatisticModel.setComCount(comCount);
				 subStatisticModel.setUnComCount(unComCount);
				 subStatisticModel.setOverComCount(overComCount);
				 subStatisticModel.setOverUnComCount(overUnComCount); 
			 }
			 catch(Exception ex){
				 ex.printStackTrace();
			 }
			request.setAttribute("StatisticModel", statisticModel); 
			request.setAttribute("TotalList", totalList); 
			request.setAttribute("SubStatisticModel", subStatisticModel); 
			request.setAttribute("Item", item); 
			return "web/statistic/efficencyStatistic";
		}

		/**
		 * 廉政监察统计
		 * @param request
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		@RequestMapping("/incorrupt/incorruptStatistic.do")
		@RequiresPermissions("statistic/incorrupt/incorruptStatistic.do")
		public String incorruptStatistic(Item item, 
				HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
			List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
			StatisticModel statisticModel = new StatisticModel();
			StatisticModel subStatisticModel = new StatisticModel();

			int totalCount = 0;
			int comCount = 0;
			int unComCount = 0;
			int overUnComCount = 0;
			int overComCount = 0;
			if(StringUtil.isEmpty(item.getSchBeginTime())){
				item.setSchBeginTime(null);
			}else{
				item.setSchBeginTime(item.getSchBeginTime()+" 00:00:00");
			}
			if(StringUtil.isEmpty(item.getSchEndTime())){
				item.setSchEndTime(null);
			}else{
				item.setSchEndTime(item.getSchEndTime()+" 23:59:59");
			}
			item.setSupervisionTypeId(Constants.SUPERVISION_TYPE_ID_LZ); 
			 try{
				 statisticModel = statisticService.loadTotalCount(new Item());
				 totalList = statisticService.loadTotalStatistisList(item);  
				 for(StatisticModel sm : totalList){
					 totalCount = totalCount + sm.getTotalCount(); 
					 comCount = comCount + sm.getComCount();
					 unComCount = unComCount+ sm.getUnComCount();
					 overUnComCount = overUnComCount+ sm.getOverUnComCount();
					 overComCount = overComCount+ sm.getOverComCount();
				 }
				 subStatisticModel.setTotalCount(totalCount);
				 subStatisticModel.setComCount(comCount);
				 subStatisticModel.setUnComCount(unComCount);
				 subStatisticModel.setOverComCount(overComCount);
				 subStatisticModel.setOverUnComCount(overUnComCount); 
			 }
			 catch(Exception ex){
				 ex.printStackTrace();
			 }
			request.setAttribute("StatisticModel", statisticModel); 
			request.setAttribute("TotalList", totalList); 
			request.setAttribute("SubStatisticModel", subStatisticModel); 
			request.setAttribute("Item", item); 
			return "web/statistic/incorruptStatistic";
		}

		/**
		 * 执法监察统计
		 * @param request
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		@RequestMapping("/enforce/enforceStatistic.do")
		@RequiresPermissions("statistic/enforce/enforceStatistic.do")
		public String enforceStatistic(Item item, 
				HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
			List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
			StatisticModel statisticModel = new StatisticModel();
			StatisticModel subStatisticModel = new StatisticModel();

			int totalCount = 0;
			int comCount = 0;
			int unComCount = 0;
			int overUnComCount = 0;
			int overComCount = 0;
			if(StringUtil.isEmpty(item.getSchBeginTime())){
				item.setSchBeginTime(null);
			}else{
				item.setSchBeginTime(item.getSchBeginTime()+" 00:00:00");
			}
			if(StringUtil.isEmpty(item.getSchEndTime())){
				item.setSchEndTime(null);
			}else{
				item.setSchEndTime(item.getSchEndTime()+" 23:59:59");
			}
			item.setSupervisionTypeId(Constants.SUPERVISION_TYPE_ID_ZF); 
			 try{
				 statisticModel = statisticService.loadTotalCount(new Item());
				 totalList = statisticService.loadTotalStatistisList(item);  
				 for(StatisticModel sm : totalList){
					 totalCount = totalCount + sm.getTotalCount(); 
					 comCount = comCount + sm.getComCount();
					 unComCount = unComCount+ sm.getUnComCount();
					 overUnComCount = overUnComCount+ sm.getOverUnComCount();
					 overComCount = overComCount+ sm.getOverComCount();
				 }
				 subStatisticModel.setTotalCount(totalCount);
				 subStatisticModel.setComCount(comCount);
				 subStatisticModel.setUnComCount(unComCount);
				 subStatisticModel.setOverComCount(overComCount);
				 subStatisticModel.setOverUnComCount(overUnComCount); 
			 }
			 catch(Exception ex){
				 ex.printStackTrace();
			 }
			request.setAttribute("StatisticModel", statisticModel); 
			request.setAttribute("TotalList", totalList); 
			request.setAttribute("SubStatisticModel", subStatisticModel); 
			request.setAttribute("Item", item); 
			return "web/statistic/enforceStatistic";
		}

		/**
		 * 分行立项统计
		 * @param request
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		@RequestMapping("/branch/branchStatistic.do")
		@RequiresPermissions("statistic/branch/branchStatistic.do")
		public String branchStatistic(Item item, 
				HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
			List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
			StatisticModel statisticModel = new StatisticModel();
			int totalCount = 0;
			int comCount = 0;
			int unComCount = 0;
			int overUnComCount = 0;
			int overComCount = 0;
			if(StringUtil.isEmpty(item.getSchBeginTime())){
				item.setSchBeginTime(null);
			}else{
				item.setSchBeginTime(item.getSchBeginTime()+" 00:00:00");
			}
			if(StringUtil.isEmpty(item.getSchEndTime())){
				item.setSchEndTime(null);
			}else{
				item.setSchEndTime(item.getSchEndTime()+" 23:59:59");
			}
			item.setPreparerOrgId(Constants.SUPERVISION_ORGAN_ID_CDFH); 
			 try{ 
				 totalList = statisticService.loadBranchTotalStatistisList(item); 
				 for(StatisticModel sm : totalList){
					 totalCount = totalCount + sm.getTotalCount(); 
					 comCount = comCount + sm.getComCount();
					 unComCount = unComCount+ sm.getUnComCount();
					 overUnComCount = overUnComCount+ sm.getOverUnComCount();
					 overComCount = overComCount+ sm.getOverComCount();
				 }
				 statisticModel.setTotalCount(totalCount);
				 statisticModel.setComCount(comCount);
				 statisticModel.setUnComCount(unComCount);
				 statisticModel.setOverComCount(overComCount);
				 statisticModel.setOverUnComCount(overUnComCount); 
			 }
			 catch(Exception ex){
				 ex.printStackTrace();
			 }
			request.setAttribute("StatisticModel", statisticModel); 
			request.setAttribute("TotalList", totalList); 
			request.setAttribute("Item", item); 
			return "web/statistic/branchStatistic";
		}

		/**
		 * 分行立项统计
		 * @param request
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		@RequestMapping("/support/supportStatistic.do")
		@RequiresPermissions("statistic/support/supportStatistic.do")
		public String supportStatistic(Item item, 
				HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
			List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
			StatisticModel statisticModel = new StatisticModel();
			int totalCount = 0;
			int comCount = 0;
			int unComCount = 0;
			int overUnComCount = 0;
			int overComCount = 0;
			if(StringUtil.isEmpty(item.getSchBeginTime())){
				item.setSchBeginTime(null);
			}else{
				item.setSchBeginTime(item.getSchBeginTime()+" 00:00:00");
			}
			if(StringUtil.isEmpty(item.getSchEndTime())){
				item.setSchEndTime(null);
			}else{
				item.setSchEndTime(item.getSchEndTime()+" 23:59:59");
			}
			item.setSuperItemType(Constants.STATIC_ITEM_TYPE_MANAGE); 
			item.setPreparerOrgId(Constants.SUPERVISION_ORGAN_ID_CDFH); 
			 try{ 
				 totalList = statisticService.loadSupportTotalStatistisList(item); 
				 for(StatisticModel sm : totalList){
					 totalCount = totalCount + sm.getTotalCount(); 
					 comCount = comCount + sm.getComCount();
					 unComCount = unComCount+ sm.getUnComCount();
					 overUnComCount = overUnComCount+ sm.getOverUnComCount();
					 overComCount = overComCount+ sm.getOverComCount();
				 }
				 statisticModel.setTotalCount(totalCount);
				 statisticModel.setComCount(comCount);
				 statisticModel.setUnComCount(unComCount);
				 statisticModel.setOverComCount(overComCount);
				 statisticModel.setOverUnComCount(overUnComCount); 
			 }
			 catch(Exception ex){
				 ex.printStackTrace();
			 }
			request.setAttribute("StatisticModel", statisticModel); 
			request.setAttribute("TotalList", totalList); 
			request.setAttribute("Item", item); 
			return "web/statistic/supportStatistic";
		}
}
