package com.rmbank.supervision.web.controller.statistic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.StatisticModelList;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.StatisticModel;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.StatisticService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/statistic")
public class StatisticController extends SystemAction {

	/**
	 * 资源注入
	 */
	@Resource
	private StatisticService statisticService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	/**
	 * 效能监察统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/efficiency/efficencyStatistic.do")
	public StatisticModelList efficencyStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>();   
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		List<Integer> orgIds=new ArrayList<Integer>();
		if(userOrg.getOrgtype() == Constants.ORG_TYPE_7 ||
				userOrg.getOrgtype() == Constants.ORG_TYPE_12){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
			//orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
		}
		
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
//			String searchName =  new String(item.getSearchName().getBytes(
//					"iso8859-1"), "utf-8");
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
			 Item newItem=new Item();
			 newItem.setOrgIds(orgIds);
			 statisticModel = statisticService.loadTotalCount(newItem);
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 
		 smList.setSubStatisticModel(subStatisticModel);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("XNJC_TJ", smList);
		return smList;
	}

	/**
	 * 廉政监察统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/incorrupt/incorruptStatistic.do")
	public StatisticModelList incorruptStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		List<Integer> orgIds=new ArrayList<Integer>();
		if(userOrg.getOrgtype() == Constants.ORG_TYPE_7 ||
				userOrg.getOrgtype() == Constants.ORG_TYPE_12){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
		}
		
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
			 Item newItem=new Item();
			 newItem.setOrgIds(orgIds);
			 statisticModel = statisticService.loadTotalCount(newItem);
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setSubStatisticModel(subStatisticModel);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("LZJC_TJ", smList);
		return smList;
	}

	/**
	 * 执法监察统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/enforce/enforceStatistic.do")
	public StatisticModelList enforceStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		List<Integer> orgIds =new ArrayList<Integer>();
		if(userOrg.getOrgtype() == Constants.ORG_TYPE_7 ||
				userOrg.getOrgtype() == Constants.ORG_TYPE_12){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
			
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
		}
		
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
			 Item newItem=new Item();
			 newItem.setOrgIds(orgIds);
			 statisticModel = statisticService.loadTotalCount(newItem);
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setSubStatisticModel(subStatisticModel);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("ZFJC_TJ", smList);
		return smList;
	}

	/**
	 * 分行立项分行完成统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/branch/branchStatistic.do")
	public StatisticModelList branchStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("FHLXFHWC_TJ", smList);
		return smList;
	}

    /**
	 * 分行立项中支完成统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/branch/branchSUPPStatistic.do")
	public StatisticModelList branchStatisticSUPP(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		
		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		if(userOrg.getOrgtype() == Constants.ORG_TYPE_7 ||
				userOrg.getOrgtype() == Constants.ORG_TYPE_12){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
		}
		
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
			 totalList = statisticService.loadBranchSUPPTotalStatistisList(item); 
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("FHLXZZWC_TJ", smList);
		return smList;
	}
	/**
	 * 中支立项统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/support/supportStatistic.do")
	public StatisticModelList supportStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		
		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		if(userOrg.getOrgtype() == Constants.ORG_TYPE_7 ||
				userOrg.getOrgtype() == Constants.ORG_TYPE_12){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
		}
		
		int totalCount = 0;
		int comCount = 0;
		int unComCount = 0;
		int overUnComCount = 0;
		int overComCount = 0;
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setTotalList(totalList);
		 HttpSession session = request.getSession();
		 session.setAttribute("ZZLXZZWC_TJ", smList);
		return smList;
	}
}
