package com.rmbank.supervision.web.controller.base;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.SystemLog;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.SysLogService;

@Scope("prototype")
@Controller
@RequestMapping("/system/log")
public class SysLogController {

	
	@Resource
	private SysLogService logService;
	@Resource
	private OrganService organService;
	/**
	 * 实时监察日志列表
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/logList.do")
	public DataListResult<SystemLog> logList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		DataListResult<SystemLog> dr = new DataListResult<SystemLog>();
		
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (systemLog.getSearchName() != null && systemLog.getSearchName() != "") {
			String searchName = URLDecoder.decode(systemLog.getSearchName(),"utf-8");
			systemLog.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (systemLog.getPageNo() == null)
			systemLog.setPageNo(1);
		systemLog.setPageSize(Constants.DEFAULT_PAGE_SIZE); 
//		if(systemLog.getMoudleId()==null){
//			
//		}
		
		if(StringUtil.isEmpty(systemLog.getSchBeginTime())){
			systemLog.setSchBeginTime(null);
		}else{
			systemLog.setSchBeginTime(systemLog.getSchBeginTime()+" 00:00:00");
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
		}else{
			systemLog.setSchEndTime(systemLog.getSchEndTime()+" 23:59:59");
		}
		
		int totalCount = 0;
		// 分页集合
		List<SystemLog> logList = new ArrayList<SystemLog>();
		try {
			// t_role取满足要求的参数数据
			logList = logService.getLogList(systemLog);
			for(SystemLog sl: logList){
				sl.setOperTimes(Constants.DATE_FORMAT.format(sl.getOperTime()));
			}
			// t_role取满足要求的记录总数
			totalCount = logService.getLogCount(systemLog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(!StringUtil.isEmpty(systemLog.getSchBeginTime()) && systemLog.getSchBeginTime().length()>9){ 
			systemLog.setSchBeginTime(systemLog.getSchBeginTime().substring(0,systemLog.getSchBeginTime().length() -9));
		}
		if(!StringUtil.isEmpty(systemLog.getSchEndTime()) && systemLog.getSchEndTime().length()>9){ 
			systemLog.setSchEndTime(systemLog.getSchEndTime().substring(0,systemLog.getSchEndTime().length() -9));
		}
		 
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		dr.setData(systemLog);
		dr.setDatalist(logList); 
		return dr;
	}
	/**
	 * 立项管理模块日志列表
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/logLXList.do")
	public DataListResult<SystemLog> logLXList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		DataListResult<SystemLog> dr = new DataListResult<SystemLog>();
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (systemLog.getSearchName() != null && systemLog.getSearchName() != "") {
			String searchName = URLDecoder.decode(systemLog.getSearchName(),"utf-8");
			systemLog.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (systemLog.getPageNo() == null)
			systemLog.setPageNo(1);
		systemLog.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		systemLog.setMoudleId(Constants.LOG_TYPE_LXGL);
		
		if(StringUtil.isEmpty(systemLog.getSchBeginTime())){
			systemLog.setSchBeginTime(null);
		}else{
			systemLog.setSchBeginTime(systemLog.getSchBeginTime()+" 00:00:00");
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
		}else{
			systemLog.setSchEndTime(systemLog.getSchEndTime()+" 23:59:59");
		}
		
		int totalCount = 0;
		// 分页集合
		List<SystemLog> logList = new ArrayList<SystemLog>();
		try {
			// t_role取满足要求的参数数据
			logList = logService.getLogList(systemLog);
			for(SystemLog sl: logList){
				sl.setOperTimes(Constants.DATE_FORMAT.format(sl.getOperTime()));
			}
			// t_role取满足要求的记录总数
			totalCount = logService.getLogCount(systemLog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		

		if(!StringUtil.isEmpty(systemLog.getSchBeginTime()) && systemLog.getSchBeginTime().length()>9){ 
			systemLog.setSchBeginTime(systemLog.getSchBeginTime().substring(0,systemLog.getSchBeginTime().length() -9));
		}
		if(!StringUtil.isEmpty(systemLog.getSchEndTime()) && systemLog.getSchEndTime().length()>9){ 
			systemLog.setSchEndTime(systemLog.getSchEndTime().substring(0,systemLog.getSchEndTime().length() -9));
		}
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		dr.setData(systemLog);
		dr.setDatalist(logList); 
		return dr;
	}

	/**
	 * 基础数据管理模块日志列表
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/logJCList.do")
	public DataListResult<SystemLog> logJCList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		DataListResult<SystemLog> dr = new DataListResult<SystemLog>();
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (systemLog.getSearchName() != null && systemLog.getSearchName() != "") {
			String searchName = URLDecoder.decode(systemLog.getSearchName(),"utf-8");
			systemLog.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (systemLog.getPageNo() == null)
			systemLog.setPageNo(1);
		systemLog.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		systemLog.setMoudleId(Constants.LOG_TYPE_BASE_DATA);
		
		if(StringUtil.isEmpty(systemLog.getSchBeginTime())){
			systemLog.setSchBeginTime(null);
		}else{
			systemLog.setSchBeginTime(systemLog.getSchBeginTime()+" 00:00:00");
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
		}else{
			systemLog.setSchEndTime(systemLog.getSchEndTime()+" 23:59:59");
		}
		
		int totalCount = 0;
		// 分页集合
		List<SystemLog> logList = new ArrayList<SystemLog>();
		try {
			// t_role取满足要求的参数数据
			logList = logService.getLogList(systemLog);
			for(SystemLog sl: logList){
				sl.setOperTimes(Constants.DATE_FORMAT.format(sl.getOperTime()));
			}
			// t_role取满足要求的记录总数
			totalCount = logService.getLogCount(systemLog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if(!StringUtil.isEmpty(systemLog.getSchBeginTime()) && systemLog.getSchBeginTime().length()>9){ 
			systemLog.setSchBeginTime(systemLog.getSchBeginTime().substring(0,systemLog.getSchBeginTime().length() -9));
		}
		if(!StringUtil.isEmpty(systemLog.getSchEndTime()) && systemLog.getSchEndTime().length()>9){ 
			systemLog.setSchEndTime(systemLog.getSchEndTime().substring(0,systemLog.getSchEndTime().length() -9));
		}
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		dr.setData(systemLog);
		dr.setDatalist(logList); 
		return dr;
	}

	/**
	 * 基础数据管理模块日志列表
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/logSysList.do")
	public DataListResult<SystemLog> logSysList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		DataListResult<SystemLog> dr = new DataListResult<SystemLog>();
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (systemLog.getSearchName() != null && systemLog.getSearchName() != "") {
			String searchName = URLDecoder.decode(systemLog.getSearchName(),"utf-8");
			systemLog.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (systemLog.getPageNo() == null)
			systemLog.setPageNo(1);
		systemLog.setPageSize(500);
		systemLog.setMoudleId(Constants.LOG_TYPE_SYS);
		
		if(StringUtil.isEmpty(systemLog.getSchBeginTime())){
			systemLog.setSchBeginTime(null);
		}else{
			systemLog.setSchBeginTime(systemLog.getSchBeginTime()+" 00:00:00");
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
		}else{
			systemLog.setSchEndTime(systemLog.getSchEndTime()+" 23:59:59");
		}
		
		int totalCount = 0;
		// 分页集合
		List<SystemLog> logList = new ArrayList<SystemLog>();
		try {
			// t_role取满足要求的参数数据
			logList = logService.getLogList(systemLog);
			for(SystemLog sl: logList){
				sl.setOperTimes(Constants.DATE_FORMAT.format(sl.getOperTime()));
			}
			// t_role取满足要求的记录总数
			totalCount = logService.getLogCount(systemLog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if(!StringUtil.isEmpty(systemLog.getSchBeginTime()) && systemLog.getSchBeginTime().length()>9){ 
			systemLog.setSchBeginTime(systemLog.getSchBeginTime().substring(0,systemLog.getSchBeginTime().length() -9));
		}
		if(!StringUtil.isEmpty(systemLog.getSchEndTime()) && systemLog.getSchEndTime().length()>9){ 
			systemLog.setSchEndTime(systemLog.getSchEndTime().substring(0,systemLog.getSchEndTime().length() -9));
		}
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		dr.setData(systemLog);
		dr.setDatalist(logList); 
		return dr;
	}
	
	/**
	 * 条件查询机构的父机构
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadPorgName.do")
	public Organ loadPorgName(
			@RequestParam(value = "orgId", required = false) Integer orgId,
			HttpServletRequest request, HttpServletResponse response) {
		Organ organ =new Organ();
		Organ thisOrgan = organService.selectByPrimaryKey(orgId);
		if(thisOrgan.getPid()!=0){
//			List<Organ> organByPId = organService.getOrganByPId(organ.getPid());
//			organ =organByPId.get(0);
			organ = organService.selectByPrimaryKey(thisOrgan.getPid());
		}
		
		return organ;// json.toString();
	}  
}
