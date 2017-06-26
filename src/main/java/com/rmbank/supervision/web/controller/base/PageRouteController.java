package com.rmbank.supervision.web.controller.base;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/pageRoute")
public class PageRouteController extends SystemAction {
	/**
     * 用户管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/user/userList.do")
    public ModelAndView userList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/user/userList");
    	return mv;
    }
    
    
    /****************************************************/
    /******************统计分析模块 开始***********************/
    /****************************************************/
    /**
     * 分行立项统计
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/statistic/branch/branchStatistic.do")
    public ModelAndView branchStatistic( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/statistic/branchStatistic");
    	return mv;
    }
    /**
     * 中支立项统计
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/statistic/support/supportStatistic.do")
    public ModelAndView supportStatistic( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/statistic/supportStatistic");
    	return mv;
    }    
    /**
     * 执法监察统计
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/statistic/enforce/enforceStatistic.do")
    public ModelAndView enforceStatistic( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/statistic/enforceStatistic");
    	return mv;
    }  
    /**
     * 廉政监察统计
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/statistic/incorrupt/incorruptStatistic.do")
    public ModelAndView incorruptStatistic( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/statistic/incorruptStatistic");
    	return mv;
    } 
    /**
     * 效能监察统计
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/statistic/efficiency/efficencyStatistic.do")
    public ModelAndView efficencyStatistic( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/statistic/efficencyStatistic");
    	return mv;
    }
    /****************************************************/
    /******************统计分析模块  结束***********************/
    /****************************************************/
    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    
    /****************************************************/
    /******************日志管理模块 开始***********************/
    /****************************************************/
    /**
     * 实时监察模块日志
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/log/logList.do")
    public ModelAndView logList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/log/logList");
    	return mv;
    }
    /**
     * 综合管理模块日志
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/log/logLXList.do")
    public ModelAndView logLXList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/log/logLXList");
    	return mv;
    }
    /**
     *基础数据模块日志
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/log/logJCList.do")
    public ModelAndView logJCList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/log/logJCList");
    	return mv;
    }   
    /**
     * 系统操作日志
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/log/logSysList.do")
    public ModelAndView logSysList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/log/logSysList");
    	return mv;
    }
    /****************************************************/
    /******************日志管理模块  结束***********************/
    /****************************************************/
    
}
