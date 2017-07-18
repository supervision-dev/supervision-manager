package com.rmbank.supervision.web.controller.export;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.ExportExcelVO;
import com.rmbank.supervision.common.StatisticModelList;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.StatisticModel;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ExportService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.StatisticService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/export/excel")
public class ExportExcelController extends SystemAction{
	
	/**
	 * 资源注入
	 */
	@Resource
	private StatisticService statisticService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private ExportService exportService;
	/**
	 * 效能监察统计
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
	@RequestMapping("/efficiency.do")
	public StatisticModelList efficencyStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		ExportExcelVO eevo = new ExportExcelVO();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>();   
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);		
		String title ="中国人民银行成都分行电子监察平台效能监察项目统计汇总表";
		if(userOrg.getOrgtype() == 42){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getId());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
			title+="（"+userOrg.getName()+"）";
			eevo.setOrgName(userOrg.getName()+"监察室");
		}else{
			title+="（全辖）";
			eevo.setOrgName(userOrg.getName());
		}
		eevo.setTitle(title);
		eevo.setFileName("效能监察统计表");
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
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA("效能监察项目");
		 eevo.setItemTdB("实时监察项目");
		 exportService.export(eevo);
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
	@RequestMapping("/incorrupt.do")
	public StatisticModelList incorruptStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		ExportExcelVO eevo = new ExportExcelVO();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		String title ="中国人民银行成都分行电子监察平台廉政监察项目统计汇总表";
		if(userOrg.getOrgtype() == 42){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getId());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
			title+="（"+userOrg.getName()+"）";
			eevo.setOrgName(userOrg.getName()+"监察室");
		}else{
			title+="（全辖）";
			eevo.setOrgName(userOrg.getName());
		}
		eevo.setTitle(title);
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
		 smList.setStatisticModel(statisticModel);
		 if(!StringUtil.isEmpty(item.getSchBeginTime()) && item.getSchBeginTime().length()>9){ 
			item.setSchBeginTime(item.getSchBeginTime().substring(0,item.getSchBeginTime().length() -9));
		 }
		 if(!StringUtil.isEmpty(item.getSchEndTime()) && item.getSchEndTime().length()>9){ 
			item.setSchEndTime(item.getSchEndTime().substring(0,item.getSchEndTime().length() -9));
		 }
		 smList.setItem(item);
		 smList.setSubStatisticModel(subStatisticModel);
		 smList.setTotalList(totalList); eevo.setSml(smList);
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA("廉政监察项目");
		 eevo.setItemTdB("实时监察项目");
		 exportService.export(eevo); 
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
	@RequestMapping("/enforce.do")
	public StatisticModelList enforceStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		ExportExcelVO eevo = new ExportExcelVO();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		StatisticModel subStatisticModel = new StatisticModel();

		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		String title ="中国人民银行成都分行电子监察平台执法监察项目统计汇总表";
		if(userOrg.getOrgtype() == 42){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getId());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
			title+="（"+userOrg.getName()+"）";
			eevo.setOrgName(userOrg.getName()+"监察室");
		}else{
			title+="（全辖）";
			eevo.setOrgName(userOrg.getName());
		}
		eevo.setTitle(title);
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
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA("执法监察项目");
		 eevo.setItemTdB("实时监察项目");
		 exportService.export(eevo); 
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
	@RequestMapping("/branchStatistic.do")
	public StatisticModelList branchStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		ExportExcelVO eevo = new ExportExcelVO();
		String title ="中国人民银行成都分行电子监察平台综合管理项目统计汇总表（分行立项分行完成）";
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
		 eevo.setOrgName("成都分行监察室");
		 eevo.setTitle(title);
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA(null);
		 eevo.setItemTdB("分行立项分行完成项目");
		 boolean export = exportService.export(eevo); 
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
	@RequestMapping("/branchSUPPStatistic.do")
	public StatisticModelList branchStatisticSUPP(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		ExportExcelVO eevo = new ExportExcelVO();
		String title ="中国人民银行成都分行电子监察平台综合管理项目统计汇总表（分行立项中支完成）";
		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		if(userOrg.getOrgtype() == 42){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getId());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
			title+="("+userOrg.getName()+")";
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
		 smList.setItem(item);
		 smList.setTotalList(totalList);
		 eevo.setOrgName("成都分行监察室");
		 eevo.setTitle(title);
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA(null);
		 eevo.setItemTdB("分行立项中支完成项目");
		 exportService.export(eevo); 
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
	@RequestMapping("/supportStatistic.do")
	public StatisticModelList supportStatistic(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		StatisticModelList smList = new StatisticModelList();
		List<StatisticModel> totalList = new ArrayList<StatisticModel>(); 
		StatisticModel statisticModel = new StatisticModel();
		ExportExcelVO eevo = new ExportExcelVO();
		User loginUser = this.getLoginUser();
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		Organ userOrg=userOrgList.get(0);
		String title ="中国人民银行成都分行电子监察平台综合管理项目统计汇总表（中支立项中支完成）";
		if(userOrg.getOrgtype() == 42){
			List<Organ> organByPId = organService.getOrganByPId(userOrg.getId());
			List<Integer> orgIds =new ArrayList<Integer>();
			for (Organ organ : organByPId) {
				orgIds.add(organ.getId());
			}
			orgIds.add(userOrg.getId());
			item.setOrgIds(orgIds);
			title+="（"+userOrg.getName()+"）";
			eevo.setOrgName(userOrg.getName()+"监察室");
		}else{
			title+="（全辖）";
			eevo.setOrgName(userOrg.getName());
		}
		eevo.setTitle(title);
		
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
		 eevo.setSml(smList);
		 eevo.setResponse(response);
		 eevo.setItemTdA(null);
		 eevo.setItemTdB("中支立项中支完成项目");
		 exportService.export(eevo); 
		return smList;
	}
	
	

}
