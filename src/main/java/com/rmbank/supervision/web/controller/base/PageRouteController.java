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
    
    
    
    
    
    
}
