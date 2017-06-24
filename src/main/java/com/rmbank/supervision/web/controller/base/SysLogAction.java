package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.SystemLog;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.SysLogService;

@Scope("prototype")
@Controller
@RequestMapping("/system/log")
public class SysLogAction {

	
	
	@Resource
	private SysLogService logService;
	/**
	 * 实时监察日志列表
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/logList.do")
	@RequiresPermissions("system/log/logList.do")
	public String logList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
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
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
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
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		request.setAttribute("SystemLog", systemLog);

		request.setAttribute("LogList", logList);

		return "web/base/log/logList";
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
	@RequestMapping(value = "/logLXList.do")
	@RequiresPermissions("system/log/logLXList.do")
	public String logLXList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
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
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
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
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		request.setAttribute("SystemLog", systemLog);

		request.setAttribute("LogList", logList);

		return "web/base/log/logLXList";
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
	@RequestMapping(value = "/logJCList.do")
	@RequiresPermissions("system/log/logJCList.do")
	public String logJCList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
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
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
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
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		request.setAttribute("SystemLog", systemLog);

		request.setAttribute("LogList", logList);

		return "web/base/log/logJCList";
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
	@RequestMapping(value = "/logSysList.do")
	@RequiresPermissions("system/log/logSysList.do")
	public String logSysList(SystemLog systemLog, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (systemLog.getSearchName() != null && systemLog.getSearchName() != "") {
			String searchName = URLDecoder.decode(systemLog.getSearchName(),"utf-8");
			systemLog.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (systemLog.getPageNo() == null)
			systemLog.setPageNo(1);
		systemLog.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		systemLog.setMoudleId(Constants.LOG_TYPE_SYS);
		if(StringUtil.isEmpty(systemLog.getSchBeginTime())){
			systemLog.setSchBeginTime(null);
		}		
		if(StringUtil.isEmpty(systemLog.getSchEndTime())){
			systemLog.setSchEndTime(null);
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
		// 通过request对象传值到前台
		systemLog.setTotalCount(totalCount);
		request.setAttribute("SystemLog", systemLog);

		request.setAttribute("LogList", logList);

		return "web/base/log/logSysList";
	}
}
