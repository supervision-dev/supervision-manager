package com.rmbank.supervision.web.controller.base;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/pageRoute")
public class PageRouteController extends SystemAction {
	
	/****************************************************/
    /******************基础数据管理模块 开始***********************/
    /****************************************************/
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

    /**
     * 角色管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/role/roleList.do")
    public ModelAndView roleList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/role/roleList");
    	return mv;
    }
    
    /**
     * 资源管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/resource/resourceList.do")
    public ModelAndView resourceList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/resource/resourceList");
    	return mv;
    }
    
    /**
     * 权限管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/permission/permissionList.do")
    public ModelAndView permissionList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/permission/permissionList");
    	return mv;
    }
    
    /**
     * 预设配置列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/config/configList.do")
    public ModelAndView configList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/config/configList");
    	return mv;
    }
    
    /**
     * 机构管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/organ/organList.do")
    public ModelAndView organList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/organ/organList");
    	return mv;
    }
    /****************************************************/
    /******************基础数据管理模块 结束***********************/
    /****************************************************/
    
    
    /****************************************************/
    /******************综合管理模块 开始***********************/
    /****************************************************/
    /**
     * 分行立项分行完成列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/manage/branch/branchFHList.do")
    public ModelAndView branchFHList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/manage/branch/branchFHList");
    	return mv;
    }
    
    /**
     * 分行立项中支完成列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/manage/branch/branchZZList.do")
    public ModelAndView branchZZList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/manage/branch/branchZZList");
    	return mv;
    }
    
    /**
     * 中支立项中支完成列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/manage/support/supportList.do")
    public ModelAndView supportList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/manage/support/supportList");
    	return mv;
    }
    
    /**
     * 量化模型管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/manage/casemanage/casemanageList.do")
    public ModelAndView casemanageList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/manage/casemanage/casemanageList");
    	return mv;
    }
    
    /**
     * 量化模型指标列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/manage/casedetail/casedetailList.do")
    public ModelAndView casedetailList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/manage/casedetail/casedetailList");
    	return mv;
    }
    /****************************************************/
    /******************综合管理模块 结束***********************/
    /****************************************************/
    
    
    
    
    
    
    
    /****************************************************/
    /******************实时监察模块 开始***********************/
    /****************************************************/
    /**
     * 效能监察列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/vision/efficiency/efficiencyList.do")
    public ModelAndView efficiencyList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/vision/efficiency/efficiencyList");
    	return mv;
    }
    
    /**
     * 效能监察查看项目
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/vision/efficiency/showItem.do")
    public ModelAndView effShowItem( 
    		@RequestParam(value="id", required = false) Integer id,
    		HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	HttpSession session = request.getSession();
    	session.setAttribute("showItemId", id);
    	ModelAndView mv = new ModelAndView("web/vision/efficiency/showItem");
    	return mv;
    }
    
    /**
     * 效能监察流程页面跳转
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/vision/efficiency/efficiencyFile.do")
    public ModelAndView efficiencyFile( 
    		@RequestParam(value="itemId", required = false) Integer itemId,
    		@RequestParam(value="tag", required = false) Integer tag,
    		HttpServletRequest request, HttpServletResponse response
    		) throws UnsupportedEncodingException { 
    	
    	HttpSession session = request.getSession();
    	session.setAttribute("efficiencyItemId", itemId);
    	
    	ModelAndView mv = new ModelAndView();
    	
    	if(tag == -1){
    		mv.setViewName("web/vision/efficiency/itemInfo");
    		
    	}
    	if(tag == 67 ){
//			ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
//			Integer contentTypeId = itemProcess.getContentTypeId();
//			if(contentTypeId==72){
//				request.setAttribute("contentID", contentTypeId);
//			}			
			mv.setViewName("web/vision/efficiency/opinion");
		}
		if(tag == 68 ){			
			mv.setViewName("web/vision/efficiency/opinion");
		}
		if(tag == 69 ){
			mv.setViewName("web/vision/efficiency/resetView");
			
		}
		if(tag==688){
			mv.setViewName("web/vision/efficiency/jianChaJieLun");
			
		}
		if(tag==777){
			mv.setViewName("web/vision/efficiency/wenZeView");
			
		}
		if(tag==778){
			mv.setViewName("web/vision/efficiency/zhengGaiView");
			
		}
		
    	return mv;
    }
    
    /**
     * 执法监察列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/vision/enforce/enforceList.do")
    public ModelAndView enforceList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/vision/enforce/enforceList");
    	return mv;
    }
    
    /**
     * 廉政监察列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/vision/incorrupt/incorruptList.do")
    public ModelAndView incorruptList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/vision/incorrupt/incorruptList");
    	return mv;
    }
    /****************************************************/
    /******************实时监察管理模块 开始***********************/
    /****************************************************/
    
    
    
    

    
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
